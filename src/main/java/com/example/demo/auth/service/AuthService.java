package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.auth.dto.ResetPasswordRequest;
import com.example.demo.auth.dto.SetPasswordRequest;
import com.example.demo.auth.internal.PasswordUtils;
import com.example.demo.auth.internal.TokenGenerator;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.multitenancy.TenantContext;
import com.example.demo.common.security.SecurityUtils;
import com.example.demo.user.entity.Role;
import com.example.demo.user.entity.TenantUserRole;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.TenantUserRoleRepository;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.TenantUserRoleService;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantUserRoleService tenantUserRoleService;
    private final PasswordUtils passwordUtils;
    private final TokenGenerator tokenGenerator;
    private final SessionService sessionService;
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    private static final String INVITE_PREFIX = "invite:";
    private static final long INVITE_TTL_HOURS = 24;

    @Transactional
    public void register(RegisterRequest request) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("Tenant context required for registration");
        }

        Optional<User> existingUser = userRepository.findByEmailAndDeletedAtIsNull(request.email());
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(request.email());
            user.setPasswordHash(passwordUtils.hash(request.password()));
            user.setFullName(request.fullName());
            user = userRepository.save(user);
        }

        // Auto-assign BUMIL role to this tenant
        Optional<TenantUserRole> existingRole = tenantUserRoleRepository
                .findByTenantIdAndUserIdAndRoleAndDeletedAtIsNull(tenantId, user.getId(), Role.BUMIL);
        if (existingRole.isEmpty()) {
            TenantUserRole tur = new TenantUserRole();
            tur.setTenantId(tenantId);
            tur.setUserId(user.getId());
            tur.setRole(Role.BUMIL);
            tenantUserRoleRepository.save(tur);
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("Tenant context required for login");
        }

        User user = userRepository.findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() -> new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        if (user.getPasswordHash() == null || !passwordUtils.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        TenantUserRole role = tenantUserRoleRepository
                .findByTenantIdAndUserIdAndRoleAndDeletedAtIsNull(tenantId, user.getId(), Role.BUMIL)
                .or(() -> tenantUserRoleRepository.findByTenantIdAndUserIdAndRoleAndDeletedAtIsNull(tenantId, user.getId(), Role.PETUGAS))
                .or(() -> tenantUserRoleRepository.findByTenantIdAndUserIdAndRoleAndDeletedAtIsNull(tenantId, user.getId(), Role.ADMIN))
                .orElseThrow(() -> new BusinessException("User does not have access to this tenant", HttpStatus.FORBIDDEN));

        String token = tokenGenerator.generateToken();
        sessionService.createSession(token, user.getId(), user.getEmail(), role.getRole().name(), tenantId);

        return new AuthResponse(token, "Bearer", 24 * 3600, user.getEmail(), role.getRole().name());
    }

    public void logout(String token) {
        sessionService.deleteSession(token);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        if (!passwordUtils.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BusinessException("Old password is incorrect");
        }

        user.setPasswordHash(passwordUtils.hash(request.newPassword()));
        userRepository.save(user);
    }

    @SneakyThrows
    @Transactional
    public void inviteAndSendEmail(String email, Role role, Long tenantId, String tenantSubdomain) {
        // Create user if not exists (without password)
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(email);
                    return userRepository.save(newUser);
                });

        // Assign role
        tenantUserRoleService.assignRole(tenantId, email, role);

        // Generate invite token
        String inviteToken = tokenGenerator.generateToken();
        String key = INVITE_PREFIX + inviteToken;
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String json = mapper.writeValueAsString(Map.of("email", email, "tenantId", tenantId));
        redisTemplate.opsForValue().set(key, json, Duration.ofHours(INVITE_TTL_HOURS));

        // Send email via Mailpit
        String setPasswordUrl = "https://" + tenantSubdomain + ".localhost:8080/api/auth/set-password?token=" + inviteToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Set Your Password");
        message.setText("Click the link to set your password: " + setPasswordUrl);
        mailSender.send(message);
    }

    @SneakyThrows
    @Transactional
    public void setPassword(SetPasswordRequest request) {
        String key = INVITE_PREFIX + request.inviteToken();
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            throw new BusinessException("Invalid or expired invite token", HttpStatus.BAD_REQUEST);
        }

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> invite = mapper.readValue(json, Map.class);
        String email = (String) invite.get("email");

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        user.setPasswordHash(passwordUtils.hash(request.newPassword()));
        userRepository.save(user);

        redisTemplate.delete(key);
    }
}
