package com.bugai.userservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
