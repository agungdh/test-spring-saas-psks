package com.example.demo.common.security;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class AuthUser implements Authentication {

    private final Long userId;
    private final String email;
    private final String role;
    private final Long tenantId;
    private final boolean authenticated;

    public AuthUser(Long userId, String email, String role, Long tenantId) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.tenantId = tenantId;
        this.authenticated = true;
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return email;
    }
}
