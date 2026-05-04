package com.bugai.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for POST /auth/register
 * Validation annotations belong on DTOs, NOT on JPA entities.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Role sent by client: DEVELOPER, ADMIN, MANAGER
    @NotBlank(message = "Role is required")
    private String role;
}