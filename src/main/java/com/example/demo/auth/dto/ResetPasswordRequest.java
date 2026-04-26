package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 6) String newPassword
) {
}
