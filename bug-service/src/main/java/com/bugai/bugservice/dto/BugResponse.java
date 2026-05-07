package com.bugai.bugservice.dto;



import com.bugai.bugservice.enums.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BugResponse DTO - returned to clients when they request bug information
 * Contains all bug details in a safe format
 * Never expose raw entity - always use this DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugResponse {

    /**
     * Unique identifier (UUID)
     */
    private UUID bugId;

    /**
     * Human-readable bug number (e.g., BUG-1001)
     */
    private String bugNumber;

    /**
     * Bug title/summary
     */
    private String title;

    /**
     * Detailed description
     */
    private String description;

    /**
     * Current status
     */
    private BugStatus status;

    /**
     * Priority level
     */
    private BugPriority priority;

    /**
     * Severity level
     */
    private BugSeverity severity;

    /**
     * Reporter's UUID
     */
    private UUID reporterId;

    /**
     * Assigned developer's UUID (null if unassigned)
     */
    private UUID assignedTo;

    /**
     * Module/component
     */
    private String module;

    /**
     * Environment
     */
    private String environment;

    /**
     * Steps to reproduce
     */
    private String stepsToReproduce;

    /**
     * Expected behavior
     */
    private String expectedBehavior;

    /**
     * Actual behavior
     */
    private String actualBehavior;

    /**
     * Tags
     */
    private String tags;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Resolution timestamp (null if not resolved)
     */
    private LocalDateTime resolvedAt;

    /**
     * Closure timestamp (null if not closed)
     */
    private LocalDateTime closedAt;

    /**
     * Active flag (soft delete indicator)
     */
    private Boolean active;
}