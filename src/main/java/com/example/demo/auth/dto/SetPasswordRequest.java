package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPasswordRequest(
        @NotBlank String inviteToken,
        @NotBlank @Size(min = 6) String newPassword
) {
}
