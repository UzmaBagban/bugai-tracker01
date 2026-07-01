package com.bugai.analyticsservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents analytics snapshots for a specific project.
 * Captures metrics like total bugs, open bugs, resolution time, etc.
 * Created periodically (daily/hourly) to track trends.
 */
@Entity
@Table(name = "bug_analytics", schema = "bug_ai_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugAnalytics {

    /**
     * Unique identifier for this analytics record (UUID generated at creation).
     */
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    /**
     * Project ID this analytics record belongs to.
     * Nullable to support system-wide analytics.
     */
    @Column(name = "project_id", columnDefinition = "CHAR(36)")
    private String projectId;

    /**
     * Total number of bugs in the project.
     */
    @Column(name = "total_bugs", nullable = false)
    @Builder.Default
    private Long totalBugs = 0L;

    /**
     * Number of open (non-resolved) bugs.
     */
    @Column(name = "open_bugs", nullable = false)
    @Builder.Default
    private Long openBugs = 0L;

    /**
     * Number of closed (resolved) bugs.
     */
    @Column(name = "closed_bugs", nullable = false)
    @Builder.Default
    private Long closedBugs = 0L;

    /**
     * Average resolution time in hours.
     */
    @Column(name = "avg_resolution_time", nullable = false)
    @Builder.Default
    private Double avgResolutionTime = 0.0;

    /**
     * Critical severity bug count.
     */
    @Column(name = "critical_bugs", nullable = false)
    @Builder.Default
    private Long criticalBugs = 0L;

    /**
     * High severity bug count.
     */
    @Column(name = "high_bugs", nullable = false)
    @Builder.Default
    private Long highBugs = 0L;

    /**
     * Medium severity bug count.
     */
    @Column(name = "medium_bugs", nullable = false)
    @Builder.Default
    private Long mediumBugs = 0L;

    /**
     * Low severity bug count.
     */
    @Column(name = "low_bugs", nullable = false)
    @Builder.Default
    private Long lowBugs = 0L;

    /**
     * Timestamp when this record was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this record was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Auto-generates UUID for the record before persistence.
     */
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}