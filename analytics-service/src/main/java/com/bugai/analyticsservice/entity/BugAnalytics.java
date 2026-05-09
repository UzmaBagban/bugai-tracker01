package com.bugai.analyticsservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing aggregated bug analytics data.
 *
 * This entity stores daily snapshots of bug metrics for analytics and reporting.
 * Each record represents the state of bugs for a specific date and can be filtered
 * by project, team, or developer.
 *
 * Database: bug_ai_analytics schema
 * Table: bug_analytics
 *
 * Indexes:
 * - Primary key on id (auto-generated)
 * - Index on analyticsDate for time-series queries
 * - Composite index on (analyticsDate, projectId) for project-specific reports
 */
@Entity
@Table(name = "bug_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugAnalytics {

    /**
     * Primary key - Auto-generated unique identifier for each analytics record.
     * Uses IDENTITY strategy for MySQL auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date this analytics snapshot represents.
     * Used for time-series analysis and trend reporting.
     * Indexed for efficient date-range queries.
     */
    @Column(nullable = false)
    private LocalDate analyticsDate;

    /**
     * Optional project identifier for project-specific analytics.
     * Null indicates system-wide analytics.
     */
    @Column(length = 100)
    private String projectId;

    /**
     * Optional team identifier for team-specific analytics.
     * Null indicates all teams or system-wide analytics.
     */
    @Column(length = 100)
    private String teamId;

    /**
     * Optional developer UUID for developer-specific analytics.
     * Null indicates all developers or system-wide analytics.
     */
    @Column(length = 36)
    private String developerUuid;

    /**
     * Total number of bugs opened on this date.
     * Represents new bug creation activity.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer bugsOpened = 0;

    /**
     * Total number of bugs resolved/closed on this date.
     * Represents bug resolution activity.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer bugsClosed = 0;

    /**
     * Total number of bugs still in OPEN status at end of this date.
     * Represents the backlog size.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer bugsOpen = 0;

    /**
     * Total number of bugs in IN_PROGRESS status at end of this date.
     * Represents work currently being done.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer bugsInProgress = 0;

    /**
     * Number of CRITICAL priority bugs in the backlog.
     * Helps track high-priority work.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer criticalBugs = 0;

    /**
     * Number of HIGH priority bugs in the backlog.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer highBugs = 0;

    /**
     * Number of MEDIUM priority bugs in the backlog.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer mediumBugs = 0;

    /**
     * Number of LOW priority bugs in the backlog.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer lowBugs = 0;

    /**
     * Average time in hours to resolve bugs closed on this date.
     * Calculated as: (sum of resolution times) / (number of bugs closed)
     * Used to track team efficiency over time.
     */
    @Column(precision = 10, scale = 2)
    private Double averageResolutionTimeHours;

    /**
     * Timestamp when this analytics record was created.
     * Automatically set on first persist.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this analytics record was last updated.
     * Automatically updated on every merge operation.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback: Set timestamps before persisting new entity.
     * Called automatically by JPA before INSERT operation.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * JPA lifecycle callback: Update the updatedAt timestamp before updating entity.
     * Called automatically by JPA before UPDATE operation.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}