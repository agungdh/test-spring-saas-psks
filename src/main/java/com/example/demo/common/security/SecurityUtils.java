package com.example.demo.common.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static AuthUser getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthUser authUser) {
            return authUser;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        AuthUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public static boolean isSuperAdmin() {
        AuthUser user = getCurrentUser();
        return user != null && user.isSuperAdmin();
    }

    public static Long getCurrentTenantId() {
        AuthUser user = getCurrentUser();
        return user != null ? user.getTenantId() : null;
    }
}
