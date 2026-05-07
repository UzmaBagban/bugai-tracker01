package com.bugai.bugservice.dto;

import com.bugai.bugservice.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * BugRequest DTO - used for creating and updating bugs
 * Contains validation rules to ensure data quality
 * Never expose this directly - convert to/from entity in service layer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugRequest {

    /**
     * Bug title - must be present and non-empty
     * Limited to 255 characters to match DB column
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    /**
     * Bug description - must be present and non-empty
     * No max length as DB uses TEXT type
     */
    @NotBlank(message = "Description is required")
    private String description;

    /**
     * Status - optional on create (defaults to OPEN)
     * Required on update
     */
    private BugStatus status;

    /**
     * Priority - optional on create (defaults to MEDIUM)
     */
    private BugPriority priority;

    /**
     * Severity - optional on create (defaults to MAJOR)
     */
    private BugSeverity severity;

    /**
     * Reporter ID - UUID of user reporting the bug
     * Required on create, cannot be changed on update
     */
    @NotNull(message = "Reporter ID is required")
    private UUID reporterId;

    /**
     * Assigned developer - optional (can be null if unassigned)
     */
    private UUID assignedTo;

    /**
     * Module/component name
     */
    @Size(max = 100, message = "Module name cannot exceed 100 characters")
    private String module;

    /**
     * Environment where bug occurred
     */
    @Size(max = 50, message = "Environment cannot exceed 50 characters")
    private String environment;

    /**
     * Steps to reproduce the bug
     */
    private String stepsToReproduce;

    /**
     * Expected behavior description
     */
    private String expectedBehavior;

    /**
     * Actual behavior observed
     */
    private String actualBehavior;

    /**
     * Comma-separated tags (e.g., "ui,login,critical")
     */
    @Size(max = 500, message = "Tags cannot exceed 500 characters")
    private String tags;
}
