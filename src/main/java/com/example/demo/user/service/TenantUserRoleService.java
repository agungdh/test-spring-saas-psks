package com.example.demo.user.service;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.multitenancy.TenantContext;
import com.example.demo.common.security.SecurityUtils;
import com.example.demo.user.entity.Role;
import com.example.demo.user.entity.TenantUserRole;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.TenantUserRoleRepository;
import com.example.demo.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantUserRoleService {

    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void assignRole(Long tenantId, String email, Role role) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException("User not found: " + email, HttpStatus.NOT_FOUND));

        Optional<TenantUserRole> existing = tenantUserRoleRepository
                .findByTenantIdAndUserIdAndRoleAndDeletedAtIsNull(tenantId, user.getId(), role);

        if (existing.isPresent()) {
            throw new BusinessException("User already has this role in tenant");
        }

        TenantUserRole tur = new TenantUserRole();
        tur.setTenantId(tenantId);
        tur.setUserId(user.getId());
        tur.setRole(role);
        tur.setCreatedBy(SecurityUtils.getCurrentUserId());
        tenantUserRoleRepository.save(tur);
    }

    @Transactional(readOnly = true)
    public Optional<Role> findRoleByTenantAndUser(Long tenantId, Long userId) {
        return tenantUserRoleRepository.findByTenantIdAndDeletedAtIsNull(tenantId)
                .stream()
                .filter(tur -> tur.getUserId().equals(userId))
                .map(TenantUserRole::getRole)
                .findFirst();
    }
}
