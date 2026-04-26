package com.example.demo.user.dto;

import com.example.demo.user.entity.Role;
import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        List<Role> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
