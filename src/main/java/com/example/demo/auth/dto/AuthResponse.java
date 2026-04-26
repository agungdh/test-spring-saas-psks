package com.example.demo.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        String email,
        String role
) {
}
