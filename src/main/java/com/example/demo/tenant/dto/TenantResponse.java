package com.example.demo.tenant.dto;

import java.time.LocalDateTime;

public record TenantResponse(
        Long id,
        String subdomain,
        String name,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
