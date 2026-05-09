package com.bugai.analyticsservice.repository;


import com.bugai.analyticsservice.entity.BugAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BugAnalytics entity.
 *
 * Provides data access methods for analytics operations including:
 * - CRUD operations (inherited from JpaRepository)
 * - Custom queries for date-range analytics
 * - Project, team, and developer-specific analytics retrieval
 * - Time-series data for trend analysis
 *
 * Spring Data JPA auto-implements all methods at runtime.
 */
@Repository
public interface BugAnalyticsRepository extends JpaRepository<BugAnalytics, Long> {

    /**
     * Find analytics for a specific date (system-wide).
     *
     * Used for retrieving daily snapshots when no specific project/team filter is needed.
     *
     * @param analyticsDate The date to retrieve analytics for
     * @return Optional containing analytics if found, empty otherwise
     */
    Optional<BugAnalytics> findByAnalyticsDate(LocalDate analyticsDate);

    /**
     * Find analytics for a specific date and project.
     *
     * Used for project-specific daily reports.
     *
     * @param analyticsDate The date to retrieve analytics for
     * @param projectId The project identifier
     * @return Optional containing analytics if found, empty otherwise
     */
    Optional<BugAnalytics> findByAnalyticsDateAndProjectId(
            LocalDate analyticsDate,
            String projectId
    );

    /**
     * Find analytics for a specific date and team.
     *
     * Used for team-specific daily reports.
     *
     * @param analyticsDate The date to retrieve analytics for
     * @param teamId The team identifier
     * @return Optional containing analytics if found, empty otherwise
     */
    Optional<BugAnalytics> findByAnalyticsDateAndTeamId(
            LocalDate analyticsDate,
            String teamId
    );

    /**
     * Find analytics for a specific date and developer.
     *
     * Used for individual developer performance tracking.
     *
     * @param analyticsDate The date to retrieve analytics for
     * @param developerUuid The developer's UUID
     * @return Optional containing analytics if found, empty otherwise
     */
    Optional<BugAnalytics> findByAnalyticsDateAndDeveloperUuid(
            LocalDate analyticsDate,
            String developerUuid
    );

    /**
     * Find all analytics within a date range, ordered by date ascending.
     *
     * Used for:
     * - Time-series trend analysis
     * - Monthly/quarterly/yearly reports
     * - Historical data visualization
     *
     * Returns data from startDate (inclusive) to endDate (inclusive).
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records ordered by date
     */
    List<BugAnalytics> findByAnalyticsDateBetweenOrderByAnalyticsDateAsc(
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Find all analytics for a specific project within a date range.
     *
     * Used for project-specific trend reports and performance tracking.
     *
     * @param projectId The project identifier
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records for the project, ordered by date
     */
    List<BugAnalytics> findByProjectIdAndAnalyticsDateBetweenOrderByAnalyticsDateAsc(
            String projectId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Find all analytics for a specific team within a date range.
     *
     * Used for team performance tracking and capacity planning.
     *
     * @param teamId The team identifier
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records for the team, ordered by date
     */
    List<BugAnalytics> findByTeamIdAndAnalyticsDateBetweenOrderByAnalyticsDateAsc(
            String teamId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Find all analytics for a specific developer within a date range.
     *
     * Used for individual developer performance reviews and workload analysis.
     *
     * @param developerUuid The developer's UUID
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records for the developer, ordered by date
     */
    List<BugAnalytics> findByDeveloperUuidAndAnalyticsDateBetweenOrderByAnalyticsDateAsc(
            String developerUuid,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Calculate total bugs opened within a date range.
     *
     * Aggregates bugsOpened across all records in the range.
     * Used for summary statistics and high-level reporting.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return Sum of all bugs opened, or 0 if no records exist
     */
    @Query("SELECT COALESCE(SUM(ba.bugsOpened), 0) FROM BugAnalytics ba " +
            "WHERE ba.analyticsDate BETWEEN :startDate AND :endDate")
    Long getTotalBugsOpenedInRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Calculate total bugs closed within a date range.
     *
     * Aggregates bugsClosed across all records in the range.
     * Used for summary statistics and throughput analysis.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return Sum of all bugs closed, or 0 if no records exist
     */
    @Query("SELECT COALESCE(SUM(ba.bugsClosed), 0) FROM BugAnalytics ba " +
            "WHERE ba.analyticsDate BETWEEN :startDate AND :endDate")
    Long getTotalBugsClosedInRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Calculate average resolution time across a date range.
     *
     * Computes the mean of averageResolutionTimeHours across all records.
     * Useful for tracking efficiency trends over time.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return Average resolution time in hours, or null if no data
     */
    @Query("SELECT AVG(ba.averageResolutionTimeHours) FROM BugAnalytics ba " +
            "WHERE ba.analyticsDate BETWEEN :startDate AND :endDate " +
            "AND ba.averageResolutionTimeHours IS NOT NULL")
    Double getAverageResolutionTimeInRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
