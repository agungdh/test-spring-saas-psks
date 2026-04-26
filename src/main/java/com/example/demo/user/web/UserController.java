package com.example.demo.user.web;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.multitenancy.TenantContext;
import com.example.demo.common.security.SecurityUtils;
import com.example.demo.tenant.entity.Tenant;
import com.example.demo.tenant.repository.TenantRepository;
import com.example.demo.user.dto.InviteRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.service.TenantUserRoleService;
import com.example.demo.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final TenantUserRoleService tenantUserRoleService;
    private final TenantRepository tenantRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PETUGAS')")
    public List<UserResponse> findAll() {
        Long tenantId = TenantContext.getTenantId();
        return userService.findAllByTenant(tenantId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PETUGAS')")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void invite(@Valid @RequestBody InviteRequest request) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        tenantUserRoleService.assignRole(tenantId, request.email(), request.role());
    }
}
