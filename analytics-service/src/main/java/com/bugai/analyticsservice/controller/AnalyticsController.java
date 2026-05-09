package com.bugai.analyticsservice.controller;


import com.bugai.analyticsservice.dto.*;
import com.bugai.analyticsservice.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Analytics Service endpoints.
 *
 * Provides HTTP API for:
 * - Creating and updating analytics records
 * - Retrieving analytics by date, project, team, or developer
 * - Getting time-series analytics data for trend analysis
 * - Calculating aggregate metrics (totals, averages)
 * - Managing analytics lifecycle
 *
 * All endpoints follow REST conventions:
 * - POST for creation (returns 201 Created)
 * - GET for retrieval (returns 200 OK)
 * - PUT for updates (returns 200 OK)
 * - DELETE for deletion (returns 204 No Content)
 *
 * Base path: /api/analytics
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    /**
     * Service layer dependency for business logic.
     * Injected via constructor by Spring's dependency injection.
     */
    private final AnalyticsService analyticsService;

    /**
     * Create a new analytics record.
     *
     * Endpoint: POST /api/analytics
     * Request Body: JSON with analytics data
     * Response: 201 Created with created analytics
     *
     * Example request:
     * {
     *   "analyticsDate": "2024-01-15",
     *   "projectId": "PROJECT-001",
     *   "bugsOpened": 5,
     *   "bugsClosed": 3,
     *   "bugsOpen": 12,
     *   "criticalBugs": 2
     * }
     *
     * @param request Analytics data (validated automatically via @Valid)
     * @return ResponseEntity with created analytics and HTTP 201
     */
    @PostMapping
    public ResponseEntity<AnalyticsResponse> createAnalytics(@Valid @RequestBody AnalyticsRequest request) {
        log.info("REST request to create analytics for date: {}", request.getAnalyticsDate());

        // Delegate to service layer for business logic
        AnalyticsResponse response = analyticsService.createAnalytics(request);

        // Return 201 Created with the created resource
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get analytics for a specific date (system-wide).
     *
     * Endpoint: GET /api/analytics/date/{date}
     * Path Variable: date in ISO format (yyyy-MM-dd)
     * Response: 200 OK with analytics data, or 404 if not found
     *
     * Example: GET /api/analytics/date/2024-01-15
     *
     * @param analyticsDate Date in ISO format (auto-parsed by Spring)
     * @return ResponseEntity with analytics data and HTTP 200
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<AnalyticsResponse> getAnalyticsByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate analyticsDate) {
        log.info("REST request to get analytics for date: {}", analyticsDate);

        AnalyticsResponse response = analyticsService.getAnalyticsByDate(analyticsDate);

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics for a specific date and project.
     *
     * Endpoint: GET /api/analytics/date/{date}/project/{projectId}
     * Path Variables: date (ISO format), projectId
     * Response: 200 OK with analytics data, or 404 if not found
     *
     * Example: GET /api/analytics/date/2024-01-15/project/PROJECT-001
     */
    @GetMapping("/date/{date}/project/{projectId}")
    public ResponseEntity<AnalyticsResponse> getAnalyticsByDateAndProject(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate analyticsDate,
            @PathVariable String projectId) {
        log.info("REST request to get analytics for date: {} and project: {}", analyticsDate, projectId);

        AnalyticsResponse response = analyticsService.getAnalyticsByDateAndProject(analyticsDate, projectId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics for a specific date and team.
     *
     * Endpoint: GET /api/analytics/date/{date}/team/{teamId}
     * Example: GET /api/analytics/date/2024-01-15/team/TEAM-BACKEND
     */
    @GetMapping("/date/{date}/team/{teamId}")
    public ResponseEntity<AnalyticsResponse> getAnalyticsByDateAndTeam(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate analyticsDate,
            @PathVariable String teamId) {
        log.info("REST request to get analytics for date: {} and team: {}", analyticsDate, teamId);

        AnalyticsResponse response = analyticsService.getAnalyticsByDateAndTeam(analyticsDate, teamId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics for a specific date and developer.
     *
     * Endpoint: GET /api/analytics/date/{date}/developer/{developerUuid}
     * Example: GET /api/analytics/date/2024-01-15/developer/550e8400-e29b-41d4-a716-446655440000
     */
    @GetMapping("/date/{date}/developer/{developerUuid}")
    public ResponseEntity<AnalyticsResponse> getAnalyticsByDateAndDeveloper(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate analyticsDate,
            @PathVariable String developerUuid) {
        log.info("REST request to get analytics for date: {} and developer: {}",
                analyticsDate, developerUuid);

        AnalyticsResponse response = analyticsService.getAnalyticsByDateAndDeveloper(
                analyticsDate, developerUuid);

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics within a date range (system-wide).
     *
     * Endpoint: GET /api/analytics/range?startDate={start}&endDate={end}
     * Query Parameters: startDate, endDate (both in ISO format)
     * Response: 200 OK with list of analytics, empty list if none found
     *
     * Example: GET /api/analytics/range?startDate=2024-01-01&endDate=2024-01-31
     *
     * Used for trend analysis, monthly reports, and time-series visualization.
     *
     * @param startDate Start of range (inclusive)
     * @param endDate End of range (inclusive)
     * @return ResponseEntity with list of analytics ordered by date
     */
    @GetMapping("/range")
    public ResponseEntity<List<AnalyticsResponse>> getAnalyticsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get analytics from {} to {}", startDate, endDate);

        List<AnalyticsResponse> responses = analyticsService.getAnalyticsInRange(startDate, endDate);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get analytics for a specific project within a date range.
     *
     * Endpoint: GET /api/analytics/project/{projectId}/range?startDate={start}&endDate={end}
     * Example: GET /api/analytics/project/PROJECT-001/range?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/project/{projectId}/range")
    public ResponseEntity<List<AnalyticsResponse>> getAnalyticsByProjectInRange(
            @PathVariable String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get analytics for project: {} from {} to {}",
                projectId, startDate, endDate);

        List<AnalyticsResponse> responses = analyticsService.getAnalyticsByProjectInRange(
                projectId, startDate, endDate);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get analytics for a specific team within a date range.
     *
     * Endpoint: GET /api/analytics/team/{teamId}/range?startDate={start}&endDate={end}
     */
    @GetMapping("/team/{teamId}/range")
    public ResponseEntity<List<AnalyticsResponse>> getAnalyticsByTeamInRange(
            @PathVariable String teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get analytics for team: {} from {} to {}",
                teamId, startDate, endDate);

        List<AnalyticsResponse> responses = analyticsService.getAnalyticsByTeamInRange(
                teamId, startDate, endDate);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get analytics for a specific developer within a date range.
     *
     * Endpoint: GET /api/analytics/developer/{developerUuid}/range?startDate={start}&endDate={end}
     */
    @GetMapping("/developer/{developerUuid}/range")
    public ResponseEntity<List<AnalyticsResponse>> getAnalyticsByDeveloperInRange(
            @PathVariable String developerUuid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get analytics for developer: {} from {} to {}",
                developerUuid, startDate, endDate);

        List<AnalyticsResponse> responses = analyticsService.getAnalyticsByDeveloperInRange(
                developerUuid, startDate, endDate);

        return ResponseEntity.ok(responses);
    }

    /**
     * Update an existing analytics record.
     *
     * Endpoint: PUT /api/analytics/{id}
     * Path Variable: id (analytics record ID)
     * Request Body: JSON with updated analytics data
     * Response: 200 OK with updated analytics, or 404 if ID not found
     *
     * Example: PUT /api/analytics/123
     *
     * @param id The ID of the analytics record to update
     * @param request Updated analytics data (validated automatically)
     * @return ResponseEntity with updated analytics and HTTP 200
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsResponse> updateAnalytics(
            @PathVariable Long id,
            @Valid @RequestBody AnalyticsRequest request) {
        log.info("REST request to update analytics with ID: {}", id);

        AnalyticsResponse response = analyticsService.updateAnalytics(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete an analytics record.
     *
     * Endpoint: DELETE /api/analytics/{id}
     * Path Variable: id (analytics record ID)
     * Response: 204 No Content if successful, 404 if ID not found
     *
     * Example: DELETE /api/analytics/123
     *
     * @param id The ID of the analytics record to delete
     * @return ResponseEntity with no body and HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalytics(@PathVariable Long id) {
        log.info("REST request to delete analytics with ID: {}", id);

        analyticsService.deleteAnalytics(id);

        // 204 No Content indicates successful deletion with no response body
        return ResponseEntity.noContent().build();
    }

    /**
     * Get aggregate metrics for a date range.
     *
     * Endpoint: GET /api/analytics/metrics?startDate={start}&endDate={end}
     * Query Parameters: startDate, endDate (both in ISO format)
     * Response: 200 OK with JSON object containing aggregate metrics
     *
     * Example: GET /api/analytics/metrics?startDate=2024-01-01&endDate=2024-01-31
     *
     * Response format:
     * {
     *   "totalBugsOpened": 150,
     *   "totalBugsClosed": 120,
     *   "averageResolutionTimeHours": 24.5
     * }
     *
     * Used for high-level summary reports and dashboards.
     *
     * @param startDate Start of range (inclusive)
     * @param endDate End of range (inclusive)
     * @return ResponseEntity with Map containing aggregate metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getAggregateMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get aggregate metrics from {} to {}", startDate, endDate);

        // Calculate all aggregate metrics
        Long totalOpened = analyticsService.getTotalBugsOpenedInRange(startDate, endDate);
        Long totalClosed = analyticsService.getTotalBugsClosedInRange(startDate, endDate);
        Double avgResolutionTime = analyticsService.getAverageResolutionTimeInRange(startDate, endDate);

        // Build response map with all metrics
        Map<String, Object> metrics = Map.of(
                "totalBugsOpened", totalOpened,
                "totalBugsClosed", totalClosed,
                "averageResolutionTimeHours", avgResolutionTime != null ? avgResolutionTime : 0.0
        );

        return ResponseEntity.ok(metrics);
    }
}