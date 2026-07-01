package com.bugai.analyticsservice.repository;

import com.bugai.analyticsservice.entity.BugAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for BugAnalytics entity.
 * Provides JPQL custom queries for analytics calculations.
 */
@Repository
public interface BugAnalyticsRepository extends JpaRepository<BugAnalytics, String> {

    /**
     * Find the most recent analytics record for a specific project.
     *
     * @param projectId UUID of the project
     * @return Optional containing the latest analytics record, or empty if none exist
     */
    @Query("SELECT ba FROM BugAnalytics ba WHERE ba.projectId = :projectId ORDER BY ba.createdAt DESC LIMIT 1")
    Optional<BugAnalytics> findLatestByProjectId(@Param("projectId") String projectId);

    /**
     * Find all analytics records for a project within a date range.
     *
     * @param projectId UUID of the project
     * @param startDate Start of the time range (inclusive)
     * @param endDate End of the time range (inclusive)
     * @return List of analytics records in ascending chronological order
     */
    @Query("SELECT ba FROM BugAnalytics ba WHERE ba.projectId = :projectId " +
            "AND ba.createdAt >= :startDate AND ba.createdAt <= :endDate " +
            "ORDER BY ba.createdAt ASC")
    List<BugAnalytics> findByProjectIdAndDateRange(
            @Param("projectId") String projectId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find all analytics records created in the last N days for a project.
     *
     * @param projectId UUID of the project
     * @param days Number of days to look back
     * @return List of analytics records
     */
    @Query("SELECT ba FROM BugAnalytics ba WHERE ba.projectId = :projectId " +
            "AND ba.createdAt >= CURRENT_TIMESTAMP - :days DAY " +
            "ORDER BY ba.createdAt DESC")
    List<BugAnalytics> findRecentAnalytics(@Param("projectId") String projectId, @Param("days") Integer days);

    /**
     * Calculate average open bug count over a period for a project.
     *
     * @param projectId UUID of the project
     * @param startDate Start of the time range
     * @param endDate End of the time range
     * @return Average number of open bugs
     */
    @Query("SELECT AVG(ba.openBugs) FROM BugAnalytics ba WHERE ba.projectId = :projectId " +
            "AND ba.createdAt >= :startDate AND ba.createdAt <= :endDate")
    Double calculateAvgOpenBugs(
            @Param("projectId") String projectId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get the trend of resolution time over a period.
     *
     * @param projectId UUID of the project
     * @param startDate Start of the time range
     * @param endDate End of the time range
     * @return List of analytics records showing resolution time trend
     */
    @Query("SELECT ba FROM BugAnalytics ba WHERE ba.projectId = :projectId " +
            "AND ba.createdAt >= :startDate AND ba.createdAt <= :endDate " +
            "ORDER BY ba.createdAt ASC")
    List<BugAnalytics> getResolutionTimeTrend(
            @Param("projectId") String projectId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}