package com.example.demo.common.multitenancy;

import com.example.demo.tenant.entity.Tenant;
import com.example.demo.tenant.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final TenantResolver tenantResolver;
    private final TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String subdomain = tenantResolver.resolveSubdomain(request);
            if (subdomain == null || subdomain.isBlank()) {
                // No subdomain — let downstream filters handle auth
                filterChain.doFilter(request, response);
                return;
            }

            Optional<Tenant> tenantOpt = tenantRepository.findBySubdomainAndDeletedAtIsNull(subdomain);
            if (tenantOpt.isEmpty() || !tenantOpt.get().isActive()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Tenant not found or inactive\"}");
                return;
            }

            TenantContext.setTenantId(tenantOpt.get().getId());
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars");
    }
}
