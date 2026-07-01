package com.bugai.analyticsservice.service;

import com.bugai.analyticsservice.dto.AnalyticsRequestDTO;
import com.bugai.analyticsservice.dto.AnalyticsResponseDTO;
import com.bugai.analyticsservice.entity.BugAnalytics;
import com.bugai.analyticsservice.exception.AnalyticsNotFoundException;
import com.bugai.analyticsservice.repository.BugAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for analytics operations.
 * Provides business logic for analytics calculations and CRUD.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsService implements AnalyticsServiceImpl {

    private final BugAnalyticsRepository analyticsRepository;

    /**
     * Create a new analytics record and persist to database.
     *
     * @param requestDTO Contains analytics data to create
     * @return Created analytics record as DTO
     */
    @Override
    public AnalyticsResponseDTO create(AnalyticsRequestDTO requestDTO) {
        // Build entity from request DTO with auto-generated UUID
        BugAnalytics analytics = BugAnalytics.builder()
                .id(UUID.randomUUID().toString())
                .projectId(requestDTO.getProjectId())
                .totalBugs(requestDTO.getTotalBugs())
                .openBugs(requestDTO.getOpenBugs())
                .closedBugs(requestDTO.getClosedBugs())
                .avgResolutionTime(requestDTO.getAvgResolutionTime())
                .criticalBugs(requestDTO.getCriticalBugs())
                .highBugs(requestDTO.getHighBugs())
                .mediumBugs(requestDTO.getMediumBugs())
                .lowBugs(requestDTO.getLowBugs())
                .build();

        // Persist and convert to DTO
        BugAnalytics savedAnalytics = analyticsRepository.save(analytics);
        return toResponse(savedAnalytics);
    }

    /**
     * Retrieve analytics record by ID.
     *
     * @param id UUID of the analytics record
     * @return Analytics data as DTO
     * @throws AnalyticsNotFoundException if record doesn't exist
     */
    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponseDTO getById(String id) {
        BugAnalytics analytics = analyticsRepository.findById(id)
                .orElseThrow(() -> new AnalyticsNotFoundException("Analytics record not found with id: " + id));
        return toResponse(analytics);
    }

    /**
     * Get the most recent analytics snapshot for a project.
     *
     * @param projectId UUID of the project
     * @return Latest analytics record as DTO
     * @throws AnalyticsNotFoundException if no records exist for the project
     */
    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponseDTO getLatestByProject(String projectId) {
        BugAnalytics analytics = analyticsRepository.findLatestByProjectId(projectId)
                .orElseThrow(() -> new AnalyticsNotFoundException("No analytics found for project: " + projectId));
        return toResponse(analytics);
    }

    /**
     * Retrieve analytics records within a specified date range for a project.
     *
     * @param projectId UUID of the project
     * @param startDate Start of range (inclusive)
     * @param endDate End of range (inclusive)
     * @return List of analytics records ordered by date (ascending)
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsResponseDTO> getAnalyticsByDateRange(String projectId, LocalDateTime startDate, LocalDateTime endDate) {
        List<BugAnalytics> analyticsList = analyticsRepository.findByProjectIdAndDateRange(projectId, startDate, endDate);
        return analyticsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get the most recent analytics records (last N days) for a project.
     * Useful for trending and recent performance analysis.
     *
     * @param projectId UUID of the project
     * @param days Number of days to look back
     * @return List of recent analytics records (most recent first)
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsResponseDTO> getRecentAnalytics(String projectId, Integer days) {
        List<BugAnalytics> analyticsList = analyticsRepository.findRecentAnalytics(projectId, days);
        return analyticsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculate average number of open bugs over a time period.
     * Useful for workload planning and sprint capacity estimation.
     *
     * @param projectId UUID of the project
     * @param startDate Start of analysis period
     * @param endDate End of analysis period
     * @return Average open bug count as Double
     */
    @Override
    @Transactional(readOnly = true)
    public Double getAvgOpenBugs(String projectId, LocalDateTime startDate, LocalDateTime endDate) {
        return analyticsRepository.calculateAvgOpenBugs(projectId, startDate, endDate);
    }

    /**
     * Get resolution time trend over a period.
     * Shows how quickly bugs are being resolved over time.
     *
     * @param projectId UUID of the project
     * @param startDate Start of analysis period
     * @param endDate End of analysis period
     * @return List of analytics records showing resolution time progression
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsResponseDTO> getResolutionTimeTrend(String projectId, LocalDateTime startDate, LocalDateTime endDate) {
        List<BugAnalytics> analyticsList = analyticsRepository.getResolutionTimeTrend(projectId, startDate, endDate);
        return analyticsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing analytics record.
     *
     * @param id UUID of the record to update
     * @param requestDTO New analytics data
     * @return Updated analytics record as DTO
     * @throws AnalyticsNotFoundException if record doesn't exist
     */
    @Override
    public AnalyticsResponseDTO update(String id, AnalyticsRequestDTO requestDTO) {
        BugAnalytics analytics = analyticsRepository.findById(id)
                .orElseThrow(() -> new AnalyticsNotFoundException("Analytics record not found with id: " + id));

        // Update fields from request DTO
        analytics.setProjectId(requestDTO.getProjectId());
        analytics.setTotalBugs(requestDTO.getTotalBugs());
        analytics.setOpenBugs(requestDTO.getOpenBugs());
        analytics.setClosedBugs(requestDTO.getClosedBugs());
        analytics.setAvgResolutionTime(requestDTO.getAvgResolutionTime());
        analytics.setCriticalBugs(requestDTO.getCriticalBugs());
        analytics.setHighBugs(requestDTO.getHighBugs());
        analytics.setMediumBugs(requestDTO.getMediumBugs());
        analytics.setLowBugs(requestDTO.getLowBugs());

        BugAnalytics updatedAnalytics = analyticsRepository.save(analytics);
        return toResponse(updatedAnalytics);
    }

    /**
     * Delete an analytics record by ID.
     *
     * @param id UUID of the record to delete
     * @throws AnalyticsNotFoundException if record doesn't exist
     */
    @Override
    public void delete(String id) {
        BugAnalytics analytics = analyticsRepository.findById(id)
                .orElseThrow(() -> new AnalyticsNotFoundException("Analytics record not found with id: " + id));
        analyticsRepository.delete(analytics);
    }

    /**
     * Private helper to convert BugAnalytics entity to AnalyticsResponseDTO.
     * Ensures consistent mapping across all service methods (DRY principle).
     *
     * @param analytics Entity to convert
     * @return Corresponding response DTO
     */
    private AnalyticsResponseDTO toResponse(BugAnalytics analytics) {
        return AnalyticsResponseDTO.builder()
                .id(analytics.getId())
                .projectId(analytics.getProjectId())
                .totalBugs(analytics.getTotalBugs())
                .openBugs(analytics.getOpenBugs())
                .closedBugs(analytics.getClosedBugs())
                .avgResolutionTime(analytics.getAvgResolutionTime())
                .criticalBugs(analytics.getCriticalBugs())
                .highBugs(analytics.getHighBugs())
                .mediumBugs(analytics.getMediumBugs())
                .lowBugs(analytics.getLowBugs())
                .createdAt(analytics.getCreatedAt())
                .updatedAt(analytics.getUpdatedAt())
                .build();
    }
}