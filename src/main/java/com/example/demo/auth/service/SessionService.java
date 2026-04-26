package com.example.demo.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SESSION_PREFIX = "session:";
    private static final long TTL_HOURS = 24;

    @SneakyThrows
    public void createSession(String token, Long userId, String email, String role, Long tenantId) {
        String key = SESSION_PREFIX + token;
        Map<String, Object> session = tenantId != null
                ? Map.of("userId", userId, "email", email, "role", role, "tenantId", tenantId)
                : Map.of("userId", userId, "email", email, "role", role);
        String json = objectMapper.writeValueAsString(session);
        redisTemplate.opsForValue().set(key, json, Duration.ofHours(TTL_HOURS));
    }

    public String getSession(String token) {
        return redisTemplate.opsForValue().get(SESSION_PREFIX + token);
    }

    public void deleteSession(String token) {
        redisTemplate.delete(SESSION_PREFIX + token);
    }

    public void refreshSession(String token) {
        redisTemplate.expire(SESSION_PREFIX + token, Duration.ofHours(TTL_HOURS));
    }
}
