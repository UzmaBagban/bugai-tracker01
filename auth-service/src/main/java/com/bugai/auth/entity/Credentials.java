package com.bugai.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Credentials entity — stored in bug_ai_auth schema.
 * Holds only authentication data. Profile data lives in User Service.
 * The UUID here is THE shared key: User Service will store this same UUID
 * as its primary key (client-sends-UUID flow).
 */
@Entity
@Table(name = "credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Credentials {

    // UUID generated here, shared with User Service as the link between services
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Each user has exactly one email — must be unique
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Stored as BCrypt hash — NEVER store plain text passwords
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Role-based access control: DEVELOPER, ADMIN, MANAGER etc.
    @Column(name = "role", nullable = false)
    private String role;

    // Soft-delete / account suspension support
    @Builder.Default  // Lombok builder respects this default value
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Audit: when the credentials record was created
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle hook — sets createdAt before first persist
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // If no ID was set externally, generate one (safety net)
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}