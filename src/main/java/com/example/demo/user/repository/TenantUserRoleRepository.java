package com.example.demo.user.repository;

import com.example.demo.user.entity.Role;
import com.example.demo.user.entity.TenantUserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantUserRoleRepository extends JpaRepository<TenantUserRole, Long> {

    Optional<TenantUserRole> findByTenantIdAndUserIdAndRoleAndDeletedAtIsNull(Long tenantId, Long userId, Role role);

    List<TenantUserRole> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    List<TenantUserRole> findByUserIdAndDeletedAtIsNull(Long userId);
}
