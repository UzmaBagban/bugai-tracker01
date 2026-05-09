package com.bugai.analyticsservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for analytics data.
 *
 * This DTO is returned to clients when:
 * - Retrieving analytics for a specific date
 * - Getting analytics reports for date ranges
 * - After creating or updating analytics records
 *
 * Never exposes internal entity details - only safe, business-relevant data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    /**
     * Unique identifier for this analytics record.
     * Used for updates or deletions.
     */
    private Long id;

    /**
     * The date this analytics data represents.
     */
    private LocalDate analyticsDate;

    /**
     * Project identifier if this is project-specific analytics.
     * Null for system-wide analytics.
     */
    private String projectId;

    /**
     * Team identifier if this is team-specific analytics.
     * Null for all-teams or system-wide analytics.
     */
    private String teamId;

    /**
     * Developer UUID if this is developer-specific analytics.
     * Null for all-developers or system-wide analytics.
     */
    private String developerUuid;

    /**
     * Total bugs opened on this date.
     */
    private Integer bugsOpened;

    /**
     * Total bugs closed on this date.
     */
    private Integer bugsClosed;

    /**
     * Total bugs in OPEN status at end of day.
     */
    private Integer bugsOpen;

    /**
     * Total bugs in IN_PROGRESS status at end of day.
     */
    private Integer bugsInProgress;

    /**
     * Number of CRITICAL priority bugs in backlog.
     */
    private Integer criticalBugs;

    /**
     * Number of HIGH priority bugs in backlog.
     */
    private Integer highBugs;

    /**
     * Number of MEDIUM priority bugs in backlog.
     */
    private Integer mediumBugs;

    /**
     * Number of LOW priority bugs in backlog.
     */
    private Integer lowBugs;

    /**
     * Average resolution time in hours for bugs closed on this date.
     * Null if no bugs were closed or metric not calculated.
     */
    private Double averageResolutionTimeHours;

    /**
     * When this analytics record was created.
     * Useful for audit and debugging purposes.
     */
    private LocalDateTime createdAt;

    /**
     * When this analytics record was last updated.
     * Useful for audit and debugging purposes.
     */
    private LocalDateTime updatedAt;
}
