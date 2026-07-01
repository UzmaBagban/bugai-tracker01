package com.bugai.analyticsservice.controller;

import com.bugai.analyticsservice.dto.AnalyticsRequestDTO;
import com.bugai.analyticsservice.dto.AnalyticsResponseDTO;
import com.bugai.analyticsservice.service.AnalyticsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Analytics Service.
 * Exposes HTTP endpoints on port 8085 for analytics operations.
 * Base URL: http://localhost:8085/api/analytics
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsServiceImpl analyticsService;

    /**
     * POST /api/analytics
     * Create a new analytics record.
     *
     * @param requestDTO Validated analytics data from request body
     * @return Created analytics record with 201 status
     */
    @PostMapping
    public ResponseEntity<AnalyticsResponseDTO> create(@Valid @RequestBody AnalyticsRequestDTO requestDTO) {
        AnalyticsResponseDTO response = analyticsService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/analytics/{id}
     * Retrieve a specific analytics record by ID.
     *
     * @param id UUID of the analytics record
     * @return Analytics record with 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsResponseDTO> getById(@PathVariable String id) {
        AnalyticsResponseDTO response = analyticsService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/analytics/project/{projectId}/latest
     * Get the most recent analytics snapshot for a project.
     * Useful for dashboard displaying current project health.
     *
     * @param projectId UUID of the project
     * @return Latest analytics record with 200 status
     */
    @GetMapping("/project/{projectId}/latest")
    public ResponseEntity<AnalyticsResponseDTO> getLatestByProject(@PathVariable String projectId) {
        AnalyticsResponseDTO response = analyticsService.getLatestByProject(projectId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/analytics/project/{projectId}/range
     * Get analytics records within a date range.
     * Query parameters: startDate (required), endDate (required)
     * Format: yyyy-MM-dd'T'HH:mm:ss
     * Example: /api/analytics/project/abc-123/range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     *
     * @param projectId UUID of the project
     * @param startDate Start of analysis period (inclusive)
     * @param endDate End of analysis period (inclusive)
     * @return List of analytics records with 200 status
     */
    @GetMapping("/project/{projectId}/range")
    public ResponseEntity<List<AnalyticsResponseDTO>> getAnalyticsByDateRange(
            @PathVariable String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AnalyticsResponseDTO> response = analyticsService.getAnalyticsByDateRange(projectId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/analytics/project/{projectId}/recent
     * Get recent analytics (last N days) for a project.
     * Query parameter: days (default 7)
     * Example: /api/analytics/project/abc-123/recent?days=30
     *
     * @param projectId UUID of the project
     * @param days Number of days to look back (default 7)
     * @return List of recent analytics records with 200 status
     */
    @GetMapping("/project/{projectId}/recent")
    public ResponseEntity<List<AnalyticsResponseDTO>> getRecentAnalytics(
            @PathVariable String projectId,
            @RequestParam(defaultValue = "7") Integer days) {
        List<AnalyticsResponseDTO> response = analyticsService.getRecentAnalytics(projectId, days);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/analytics/project/{projectId}/avg-open-bugs
     * Calculate average open bugs over a period.
     * Query parameters: startDate (required), endDate (required)
     * Useful for workload planning.
     *
     * @param projectId UUID of the project
     * @param startDate Start of analysis period
     * @param endDate End of analysis period
     * @return Average open bug count as Double with 200 status
     */
    @GetMapping("/project/{projectId}/avg-open-bugs")
    public ResponseEntity<Double> getAvgOpenBugs(
            @PathVariable String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double avgOpenBugs = analyticsService.getAvgOpenBugs(projectId, startDate, endDate);
        return ResponseEntity.ok(avgOpenBugs);
    }

    /**
     * GET /api/analytics/project/{projectId}/resolution-trend
     * Get resolution time trend over a period.
     * Shows how quickly bugs are being resolved over time.
     * Query parameters: startDate (required), endDate (required)
     *
     * @param projectId UUID of the project
     * @param startDate Start of analysis period
     * @param endDate End of analysis period
     * @return List of analytics records showing trend with 200 status
     */
    @GetMapping("/project/{projectId}/resolution-trend")
    public ResponseEntity<List<AnalyticsResponseDTO>> getResolutionTimeTrend(
            @PathVariable String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AnalyticsResponseDTO> response = analyticsService.getResolutionTimeTrend(projectId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/analytics/{id}
     * Update an existing analytics record.
     *
     * @param id UUID of the record to update
     * @param requestDTO Updated analytics data
     * @return Updated analytics record with 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody AnalyticsRequestDTO requestDTO) {
        AnalyticsResponseDTO response = analyticsService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/analytics/{id}
     * Delete an analytics record by ID.
     *
     * @param id UUID of the record to delete
     * @return 204 No Content status on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        analyticsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}