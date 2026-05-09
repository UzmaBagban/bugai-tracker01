package com.bugai.analyticsservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating analytics data.
 *
 * This DTO is used when:
 * - Recording daily bug metrics
 * - Updating existing analytics snapshots
 * - Batch importing historical analytics data
 *
 * Validation ensures data integrity before persisting to database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsRequest {

    /**
     * The date this analytics data represents.
     * Required field - cannot be null.
     * Should be a valid date, typically today or a past date.
     */
    @NotNull(message = "Analytics date is required")
    private LocalDate analyticsDate;

    /**
     * Optional project identifier.
     * When provided, analytics are scoped to this specific project.
     * When null, represents system-wide or all-projects analytics.
     */
    private String projectId;

    /**
     * Optional team identifier.
     * When provided, analytics are scoped to this specific team.
     * When null, represents all teams or system-wide analytics.
     */
    private String teamId;

    /**
     * Optional developer UUID.
     * When provided, analytics are scoped to this specific developer.
     * When null, represents all developers or system-wide analytics.
     */
    private String developerUuid;

    /**
     * Number of bugs opened on this date.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer bugsOpened = 0;

    /**
     * Number of bugs closed on this date.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer bugsClosed = 0;

    /**
     * Number of bugs in OPEN status at end of day.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer bugsOpen = 0;

    /**
     * Number of bugs in IN_PROGRESS status at end of day.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer bugsInProgress = 0;

    /**
     * Number of CRITICAL priority bugs in backlog.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer criticalBugs = 0;

    /**
     * Number of HIGH priority bugs in backlog.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer highBugs = 0;

    /**
     * Number of MEDIUM priority bugs in backlog.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer mediumBugs = 0;

    /**
     * Number of LOW priority bugs in backlog.
     * Defaults to 0 if not provided.
     */
    @Builder.Default
    private Integer lowBugs = 0;

    /**
     * Average time in hours to resolve bugs closed on this date.
     * Optional - can be null if no bugs were closed.
     * Should be a positive number when provided.
     */
    private Double averageResolutionTimeHours;
}