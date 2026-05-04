package com.bugai.auth.dto;



import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Response DTO returned after register or login.
 * NEVER expose passwordHash or raw entity fields.
 * The credentialsId (UUID) is what the client sends to User Service
 * to link the two records.
 */
@Data
@Builder
public class AuthResponse {

    // The shared UUID — client sends this to POST /users
    private UUID credentialsId;

    private String email;
    private String role;
    private boolean active;

    // Phase 2: JWT token will be added here
    // private String token;
}
