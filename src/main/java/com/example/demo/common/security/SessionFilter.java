package com.example.demo.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SESSION_PREFIX = "session:";
    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        String sessionKey = SESSION_PREFIX + token;
        String sessionJson = redisTemplate.opsForValue().get(sessionKey);

        if (sessionJson == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> session = objectMapper.readValue(sessionJson, Map.class);
            Long userId = ((Number) session.get("userId")).longValue();
            String email = (String) session.get("email");
            String role = (String) session.get("role");
            Long tenantId = session.get("tenantId") != null
                    ? ((Number) session.get("tenantId")).longValue()
                    : null;

            // Validate tenant match unless SUPER_ADMIN
            if (!"SUPER_ADMIN".equals(role)) {
                Long currentTenantId = com.example.demo.common.multitenancy.TenantContext.getTenantId();
                if (currentTenantId != null && !currentTenantId.equals(tenantId)) {
                    sendError(response, HttpServletResponse.SC_FORBIDDEN, "Token not valid for this tenant");
                    return;
                }
            }

            AuthUser authUser = new AuthUser(userId, email, role, tenantId);
            SecurityContextHolder.getContext().setAuthentication(authUser);

            // Refresh sliding TTL
            String ttlStr = System.getProperty("app.session.ttl-hours", "24");
            long ttlHours = Long.parseLong(ttlStr);
            redisTemplate.expire(sessionKey, Duration.ofHours(ttlHours));

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Session parsing error", e);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs");
    }
}
