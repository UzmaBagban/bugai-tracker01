package com.bugai.analyticsservice.service;


import com.bugai.analyticsservice.dto.*;
import com.bugai.analyticsservice.exception.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for analytics operations.
 *
 * Defines business logic for:
 * - Recording daily analytics snapshots
 * - Retrieving analytics for specific dates or date ranges
 * - Calculating aggregate metrics (totals, averages)
 * - Filtering analytics by project, team, or developer
 *
 * Implementation handles:
 * - Data validation
 * - Entity-DTO conversion
 * - Business rule enforcement
 * - Transaction management
 */
public interface AnalyticsService {

    /**
     * Create a new analytics record for a specific date.
     *
     * Typically called at end of day or by a scheduled job to snapshot
     * the current state of bugs in the system.
     *
     * @param request Analytics data to record
     * @return Created analytics record with generated ID
     * @throws IllegalArgumentException if request data is invalid
     */
    AnalyticsResponse createAnalytics(AnalyticsRequest request);

    /**
     * Retrieve analytics for a specific date (system-wide).
     *
     * @param analyticsDate The date to retrieve analytics for
     * @return Analytics for the date if found
     * @throws com.bugai.analytics.exception.ResourceNotFoundException if not found
     */
    AnalyticsResponse getAnalyticsByDate(LocalDate analyticsDate);

    /**
     * Retrieve analytics for a specific date and project.
     *
     * @param analyticsDate The date to retrieve analytics for
     * @param projectId The project identifier
     * @return Analytics for the date and project if found
     * @throws com.bugai.analytics.exception.ResourceNotFoundException if not found
     */
    AnalyticsResponse getAnalyticsByDateAndProject(LocalDate analyticsDate, String projectId);

    /**
     * Retrieve analytics for a specific date and team.
     *
     * @param analyticsDate The date to retrieve analytics for
     * @param teamId The team identifier
     * @return Analytics for the date and team if found
     * @throws com.bugai.analytics.exception.ResourceNotFoundException if not found
     */
    AnalyticsResponse getAnalyticsByDateAndTeam(LocalDate analyticsDate, String teamId);

    /**
     * Retrieve analytics for a specific date and developer.
     *
     * @param analyticsDate The date to retrieve analytics for
     * @param developerUuid The developer's UUID
     * @return Analytics for the date and developer if found
     * @throws com.bugai.analytics.exception.ResourceNotFoundException if not found
     */
    AnalyticsResponse getAnalyticsByDateAndDeveloper(LocalDate analyticsDate, String developerUuid);

    /**
     * Retrieve all analytics within a date range (system-wide).
     *
     * Used for trend analysis, monthly reports, and historical data visualization.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records ordered by date, empty list if none found
     */
    List<AnalyticsResponse> getAnalyticsInRange(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieve analytics for a specific project within a date range.
     *
     * @param projectId The project identifier
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records for the project, ordered by date
     */
    List<AnalyticsResponse> getAnalyticsByProjectInRange(
            String projectId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Retrieve analytics for a specific team within a date range.
     *
     * @param teamId The team identifier
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records for the team, ordered by date
     */
    List<AnalyticsResponse> getAnalyticsByTeamInRange(
            String teamId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Retrieve analytics for a specific developer within a date range.
     *
     * @param developerUuid The developer's UUID
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of analytics records for the developer, ordered by date
     */
    List<AnalyticsResponse> getAnalyticsByDeveloperInRange(
            String developerUuid,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Update an existing analytics record.
     *
     * Used to correct or update analytics data after initial recording.
     *
     * @param id The ID of the analytics record to update
     * @param request Updated analytics data
     * @return Updated analytics record
     * @throws com.bugai.analytics.exception.ResourceNotFoundException if ID not found
     */
    AnalyticsResponse updateAnalytics(Long id, AnalyticsRequest request);

    /**
     * Delete an analytics record.
     *
     * Typically used to remove erroneous or duplicate analytics entries.
     *
     * @param id The ID of the analytics record to delete
     * @throws com.bugai.analytics.exception.ResourceNotFoundException if ID not found
     */
    void deleteAnalytics(Long id);

    /**
     * Calculate total bugs opened within a date range.
     *
     * Aggregates across all analytics records in the range.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return Total number of bugs opened in the range
     */
    Long getTotalBugsOpenedInRange(LocalDate startDate, LocalDate endDate);

    /**
     * Calculate total bugs closed within a date range.
     *
     * Aggregates across all analytics records in the range.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return Total number of bugs closed in the range
     */
    Long getTotalBugsClosedInRange(LocalDate startDate, LocalDate endDate);

    /**
     * Calculate average resolution time across a date range.
     *
     * Computes mean resolution time from all records with valid data.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return Average resolution time in hours, or null if no data
     */
    Double getAverageResolutionTimeInRange(LocalDate startDate, LocalDate endDate);
}