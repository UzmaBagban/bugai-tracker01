package com.bugai.bugservice.entity;

import com.bugai.bugservice.enums.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bug entity - represents a bug/defect in the system
 * Maps to 'bugs' table in bug_ai_bug schema
 * Uses UUID as primary key for distributed system compatibility
 */
@Entity
@Table(name = "bugs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bug {

    /**
     * Primary key - UUID format
     * Generated automatically on persist
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bug_id", updatable = false, nullable = false)
    private UUID bugId;

    /**
     * Human-readable bug identifier (e.g., BUG-1001, BUG-1002)
     * Auto-generated, unique, and indexed for fast lookups
     */
    @Column(name = "bug_number", unique = true, nullable = false, length = 20)
    private String bugNumber;

    /**
     * Brief title/summary of the bug
     */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * Detailed description of the bug
     * TEXT type allows large content
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Current status of the bug (OPEN, IN_PROGRESS, etc.)
     * Stored as string in DB, mapped to enum in Java
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BugStatus status = BugStatus.OPEN;

    /**
     * Priority level (LOW, MEDIUM, HIGH, CRITICAL)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private BugPriority priority = BugPriority.MEDIUM;

    /**
     * Severity level (MINOR, MAJOR, CRITICAL, BLOCKER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    @Builder.Default
    private BugSeverity severity = BugSeverity.MAJOR;

    /**
     * UUID of the user who reported this bug
     * Foreign key relationship (not enforced by JPA, managed at service level)
     */
    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    /**
     * UUID of the developer assigned to fix this bug
     * Can be null if not yet assigned
     */
    @Column(name = "assigned_to")
    private UUID assignedTo;

    /**
     * Module/component where bug was found (e.g., "User Service", "Auth Module")
     */
    @Column(name = "module", length = 100)
    private String module;

    /**
     * Environment where bug occurred (DEV, STAGING, PRODUCTION)
     */
    @Column(name = "environment", length = 50)
    private String environment;

    /**
     * Steps to reproduce the bug
     */
    @Column(name = "steps_to_reproduce", columnDefinition = "TEXT")
    private String stepsToReproduce;

    /**
     * Expected behavior
     */
    @Column(name = "expected_behavior", columnDefinition = "TEXT")
    private String expectedBehavior;

    /**
     * Actual behavior observed
     */
    @Column(name = "actual_behavior", columnDefinition = "TEXT")
    private String actualBehavior;

    /**
     * Comma-separated tags for categorization
     */
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * Timestamp when bug was created
     * Automatically set by Hibernate on persist
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when bug was last updated
     * Automatically updated by Hibernate on merge
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Timestamp when bug was resolved
     * Null if not yet resolved
     */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /**
     * Timestamp when bug was closed
     * Null if not yet closed
     */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /**
     * Soft delete flag - true means bug is active
     * Allows for "deletion" without actually removing data
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}