package com.example.demo.tenant.dto;

import jakarta.validation.constraints.NotBlank;

public record TenantRequest(
        @NotBlank String subdomain,
        @NotBlank String name
) {
}
