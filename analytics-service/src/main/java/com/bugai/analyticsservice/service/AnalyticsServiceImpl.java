package com.bugai.analyticsservice.service;

import com.bugai.analyticsservice.dto.AnalyticsRequestDTO;
import com.bugai.analyticsservice.dto.AnalyticsResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for analytics operations.
 * Handles CRUD and analytics calculations.
 */
public interface AnalyticsServiceImpl {

    /**
     * Create a new analytics record.
     */
    AnalyticsResponseDTO create(AnalyticsRequestDTO requestDTO);

    /**
     * Get analytics record by ID.
     */
    AnalyticsResponseDTO getById(String id);

    /**
     * Get the most recent analytics for a project.
     */
    AnalyticsResponseDTO getLatestByProject(String projectId);

    /**
     * Get analytics for a project within a date range.
     */
    List<AnalyticsResponseDTO> getAnalyticsByDateRange(String projectId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get recent analytics (last N days) for a project.
     */
    List<AnalyticsResponseDTO> getRecentAnalytics(String projectId, Integer days);

    /**
     * Calculate average open bugs over a period.
     */
    Double getAvgOpenBugs(String projectId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get resolution time trend.
     */
    List<AnalyticsResponseDTO> getResolutionTimeTrend(String projectId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Update an analytics record.
     */
    AnalyticsResponseDTO update(String id, AnalyticsRequestDTO requestDTO);

    /**
     * Delete an analytics record.
     */
    void delete(String id);
}