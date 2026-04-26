package com.example.demo.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String email,
        @NotBlank String fullName
) {
}
