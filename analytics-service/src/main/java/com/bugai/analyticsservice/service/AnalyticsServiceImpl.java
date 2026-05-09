package com.bugai.analyticsservice.service;



import com.bugai.analyticsservice.dto.*;
import com.bugai.analyticsservice.exception.*;
import com.bugai.analyticsservice.entity.BugAnalytics;
import com.bugai.analyticsservice.repository.BugAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AnalyticsService interface.
 *
 * Handles all business logic for analytics operations:
 * - Creating daily analytics snapshots
 * - Retrieving analytics data with various filters
 * - Calculating aggregate metrics
 * - Managing analytics lifecycle (CRUD operations)
 *
 * @Transactional ensures all write operations are atomic and consistent.
 * @RequiredArgsConstructor generates constructor for final fields (dependency injection).
 * @Slf4j provides logger instance for debugging and monitoring.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    /**
     * Repository for database operations on BugAnalytics entity.
     * Injected via constructor by Spring's dependency injection.
     */
    private final BugAnalyticsRepository analyticsRepository;

    /**
     * Create a new analytics record.
     *
     * Flow:
     * 1. Log the creation attempt
     * 2. Convert request DTO to entity
     * 3. Persist entity to database (transaction ensures atomicity)
     * 4. Convert saved entity to response DTO
     * 5. Log success and return
     *
     * @param request Analytics data to record
     * @return Created analytics with generated ID and timestamps
     */
    @Override
    @Transactional
    public AnalyticsResponse createAnalytics(AnalyticsRequest request) {
        log.info("Creating analytics for date: {}", request.getAnalyticsDate());

        // Build entity from request DTO
        // Builder pattern ensures all fields are set correctly
        BugAnalytics analytics = BugAnalytics.builder()
                .analyticsDate(request.getAnalyticsDate())
                .projectId(request.getProjectId())
                .teamId(request.getTeamId())
                .developerUuid(request.getDeveloperUuid())
                .bugsOpened(request.getBugsOpened())
                .bugsClosed(request.getBugsClosed())
                .bugsOpen(request.getBugsOpen())
                .bugsInProgress(request.getBugsInProgress())
                .criticalBugs(request.getCriticalBugs())
                .highBugs(request.getHighBugs())
                .mediumBugs(request.getMediumBugs())
                .lowBugs(request.getLowBugs())
                .averageResolutionTimeHours(request.getAverageResolutionTimeHours())
                .build();

        // Persist to database
        // JPA automatically sets createdAt and updatedAt via @PrePersist callback
        BugAnalytics saved = analyticsRepository.save(analytics);

        log.info("Analytics created successfully with ID: {}", saved.getId());

        // Convert entity to response DTO before returning
        return toResponse(saved);
    }

    /**
     * Get analytics for a specific date (system-wide).
     *
     * @throws ResourceNotFoundException if no analytics exist for the date
     */
    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalyticsByDate(LocalDate analyticsDate) {
        log.info("Fetching analytics for date: {}", analyticsDate);

        // Query database for analytics on this date
        // Optional pattern handles existence check elegantly
        BugAnalytics analytics = analyticsRepository.findByAnalyticsDate(analyticsDate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analytics not found for date: " + analyticsDate));

        return toResponse(analytics);
    }

    /**
     * Get analytics for a specific date and project.
     *
     * @throws ResourceNotFoundException if no analytics exist for the date/project combination
     */
    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalyticsByDateAndProject(LocalDate analyticsDate, String projectId) {
        log.info("Fetching analytics for date: {} and project: {}", analyticsDate, projectId);

        BugAnalytics analytics = analyticsRepository
                .findByAnalyticsDateAndProjectId(analyticsDate, projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analytics not found for date: " + analyticsDate + " and project: " + projectId));

        return toResponse(analytics);
    }

    /**
     * Get analytics for a specific date and team.
     *
     * @throws ResourceNotFoundException if no analytics exist for the date/team combination
     */
    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalyticsByDateAndTeam(LocalDate analyticsDate, String teamId) {
        log.info("Fetching analytics for date: {} and team: {}", analyticsDate, teamId);

        BugAnalytics analytics = analyticsRepository
                .findByAnalyticsDateAndTeamId(analyticsDate, teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analytics not found for date: " + analyticsDate + " and team: " + teamId));

        return toResponse(analytics);
    }

    /**
     * Get analytics for a specific date and developer.
     *
     * @throws ResourceNotFoundException if no analytics exist for the date/developer combination
     */
    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalyticsByDateAndDeveloper(LocalDate analyticsDate, String developerUuid) {
        log.info("Fetching analytics for date: {} and developer: {}", analyticsDate, developerUuid);

        BugAnalytics analytics = analyticsRepository
                .findByAnalyticsDateAndDeveloperUuid(analyticsDate, developerUuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analytics not found for date: " + analyticsDate + " and developer: " + developerUuid));

        return toResponse(analytics);
    }

    /**
     * Get all analytics within a date range (system-wide).
     *
     * Returns empty list if no records found (not an error condition).
     * Results are ordered by date ascending for time-series analysis.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsResponse> getAnalyticsInRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching analytics from {} to {}", startDate, endDate);

        // Query returns ordered list from repository
        List<BugAnalytics> analyticsList = analyticsRepository
                .findByAnalyticsDateBetweenOrderByAnalyticsDateAsc(startDate, endDate);

        // Stream API for efficient entity-to-DTO conversion
        return analyticsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get analytics for a specific project within a date range.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsResponse> getAnalyticsByProjectInRange(
            String projectId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching analytics for project: {} from {} to {}", projectId, startDate, endDate);

        List<BugAnalytics> analyticsList = analyticsRepository
                .findByProjectIdAndAnalyticsDateBetweenOrderByAnalyticsDateAsc(
                        projectId, startDate, endDate);

        return analyticsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get analytics for a specific team within a date range.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsResponse> getAnalyticsByTeamInRange(
            String teamId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching analytics for team: {} from {} to {}", teamId, startDate, endDate);

        List<BugAnalytics> analyticsList = analyticsRepository
                .findByTeamIdAndAnalyticsDateBetweenOrderByAnalyticsDateAsc(
                        teamId, startDate, endDate);

        return analyticsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get analytics for a specific developer within a date range.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsResponse> getAnalyticsByDeveloperInRange(
            String developerUuid, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching analytics for developer: {} from {} to {}",
                developerUuid, startDate, endDate);

        List<BugAnalytics> analyticsList = analyticsRepository
                .findByDeveloperUuidAndAnalyticsDateBetweenOrderByAnalyticsDateAsc(
                        developerUuid, startDate, endDate);

        return analyticsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing analytics record.
     *
     * Flow:
     * 1. Find existing record by ID (throw exception if not found)
     * 2. Update all fields from request DTO
     * 3. Save updated entity (transaction ensures atomicity)
     * 4. Return updated response DTO
     *
     * Note: JPA automatically updates the updatedAt timestamp via @PreUpdate callback
     */
    @Override
    @Transactional
    public AnalyticsResponse updateAnalytics(Long id, AnalyticsRequest request) {
        log.info("Updating analytics with ID: {}", id);

        // Find existing record or throw exception
        BugAnalytics analytics = analyticsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics not found with ID: " + id));

        // Update all fields from request
        // Using setters generated by Lombok's @Setter
        analytics.setAnalyticsDate(request.getAnalyticsDate());
        analytics.setProjectId(request.getProjectId());
        analytics.setTeamId(request.getTeamId());
        analytics.setDeveloperUuid(request.getDeveloperUuid());
        analytics.setBugsOpened(request.getBugsOpened());
        analytics.setBugsClosed(request.getBugsClosed());
        analytics.setBugsOpen(request.getBugsOpen());
        analytics.setBugsInProgress(request.getBugsInProgress());
        analytics.setCriticalBugs(request.getCriticalBugs());
        analytics.setHighBugs(request.getHighBugs());
        analytics.setMediumBugs(request.getMediumBugs());
        analytics.setLowBugs(request.getLowBugs());
        analytics.setAverageResolutionTimeHours(request.getAverageResolutionTimeHours());

        // Save updated entity
        BugAnalytics updated = analyticsRepository.save(analytics);

        log.info("Analytics updated successfully with ID: {}", id);

        return toResponse(updated);
    }

    /**
     * Delete an analytics record.
     *
     * Flow:
     * 1. Verify record exists (throw exception if not)
     * 2. Delete from database (transaction ensures atomicity)
     * 3. Log success
     *
     * @throws ResourceNotFoundException if ID doesn't exist
     */
    @Override
    @Transactional
    public void deleteAnalytics(Long id) {
        log.info("Deleting analytics with ID: {}", id);

        // Verify existence before deletion
        if (!analyticsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Analytics not found with ID: " + id);
        }

        // Delete from database
        analyticsRepository.deleteById(id);

        log.info("Analytics deleted successfully with ID: {}", id);
    }

    /**
     * Calculate total bugs opened in a date range.
     *
     * Delegates to repository's aggregate query.
     * Returns 0 if no records exist in range.
     */
    @Override
    @Transactional(readOnly = true)
    public Long getTotalBugsOpenedInRange(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating total bugs opened from {} to {}", startDate, endDate);
        return analyticsRepository.getTotalBugsOpenedInRange(startDate, endDate);
    }

    /**
     * Calculate total bugs closed in a date range.
     *
     * Delegates to repository's aggregate query.
     * Returns 0 if no records exist in range.
     */
    @Override
    @Transactional(readOnly = true)
    public Long getTotalBugsClosedInRange(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating total bugs closed from {} to {}", startDate, endDate);
        return analyticsRepository.getTotalBugsClosedInRange(startDate, endDate);
    }

    /**
     * Calculate average resolution time in a date range.
     *
     * Delegates to repository's aggregate query.
     * Returns null if no valid data exists in range.
     */
    @Override
    @Transactional(readOnly = true)
    public Double getAverageResolutionTimeInRange(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating average resolution time from {} to {}", startDate, endDate);
        return analyticsRepository.getAverageResolutionTimeInRange(startDate, endDate);
    }

    /**
     * Private helper method to convert BugAnalytics entity to AnalyticsResponse DTO.
     *
     * This pattern ensures:
     * - Entities are never exposed to clients
     * - Response structure is consistent
     * - Internal fields (like database IDs) are properly handled
     *
     * Uses Lombok's @Builder for clean, readable object construction.
     *
     * @param analytics The entity to convert
     * @return Response DTO with all relevant fields populated
     */
    private AnalyticsResponse toResponse(BugAnalytics analytics) {
        return AnalyticsResponse.builder()
                .id(analytics.getId())
                .analyticsDate(analytics.getAnalyticsDate())
                .projectId(analytics.getProjectId())
                .teamId(analytics.getTeamId())
                .developerUuid(analytics.getDeveloperUuid())
                .bugsOpened(analytics.getBugsOpened())
                .bugsClosed(analytics.getBugsClosed())
                .bugsOpen(analytics.getBugsOpen())
                .bugsInProgress(analytics.getBugsInProgress())
                .criticalBugs(analytics.getCriticalBugs())
                .highBugs(analytics.getHighBugs())
                .mediumBugs(analytics.getMediumBugs())
                .lowBugs(analytics.getLowBugs())
                .averageResolutionTimeHours(analytics.getAverageResolutionTimeHours())
                .createdAt(analytics.getCreatedAt())
                .updatedAt(analytics.getUpdatedAt())
                .build();
    }
}