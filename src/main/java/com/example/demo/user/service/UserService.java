package com.example.demo.user.service;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.multitenancy.TenantContext;
import com.example.demo.common.security.SecurityUtils;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.entity.Role;
import com.example.demo.user.entity.TenantUserRole;
import com.example.demo.user.entity.User;
import com.example.demo.user.mapper.UserMapper;
import com.example.demo.user.repository.TenantUserRoleRepository;
import com.example.demo.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponse> findAllByTenant(Long tenantId) {
        List<TenantUserRole> roles = tenantUserRoleRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
        return roles.stream()
                .map(TenantUserRole::getUserId)
                .distinct()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    List<Role> userRoles = roles.stream()
                            .filter(r -> r.getUserId().equals(user.getId()))
                            .map(TenantUserRole::getRole)
                            .collect(Collectors.toList());
                    return new UserResponse(
                            user.getId(),
                            user.getEmail(),
                            user.getFullName(),
                            userRoles,
                            user.getCreatedAt(),
                            user.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        List<Role> roles = tenantUserRoleRepository.findByUserIdAndDeletedAtIsNull(id)
                .stream()
                .map(TenantUserRole::getRole)
                .collect(Collectors.toList());
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), roles,
                user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
            throw new BusinessException("Email already exists");
        }
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
}
