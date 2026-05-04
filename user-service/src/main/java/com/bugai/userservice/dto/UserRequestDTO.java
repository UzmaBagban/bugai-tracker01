
package com.bugai.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    private UUID id; // comes from Auth Service

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(
            regexp = "^[0-9+]{10,15}$",
            message = "Phone must contain only digits and optional +"
    )
    private String phone;
}

