package com.bugai.bugservice.service;


import com.bugai.bugservice.dto.*;
import com.bugai.bugservice.enums.*;
import com.bugai.bugservice.exception.*;

import java.util.List;
import java.util.UUID;

/**
 * BugService interface - defines all business operations for bug management
 * Implementation will be provided by BugServiceImpl
 * Interface allows for easy mocking in tests and future implementations
 */
public interface BugService {

    /**
     * Create a new bug
     * Generates bug number automatically (BUG-1001, BUG-1002, etc.)
     * Sets default values for status, priority, severity if not provided
     *
     * @param request Bug details
     * @return Created bug with generated ID and bug number
     */
    BugResponse createBug(BugRequest request);

    /**
     * Update an existing bug
     * Allows updating all fields except bugId, bugNumber, reporterId, and timestamps
     * Updates resolvedAt/closedAt based on status changes
     *
     * @param bugId Bug UUID
     * @param request Updated bug details
     * @return Updated bug
     * @throws ResourceNotFoundException if bug doesn't exist
     */
    BugResponse updateBug(UUID bugId, BugRequest request);

    /**
     * Get bug by UUID
     *
     * @param bugId Bug UUID
     * @return Bug details
     * @throws ResourceNotFoundException if bug doesn't exist
     */
    BugResponse getBugById(UUID bugId);

    /**
     * Get bug by bug number (e.g., BUG-1001)
     *
     * @param bugNumber Human-readable bug number
     * @return Bug details
     * @throws ResourceNotFoundException if bug doesn't exist
     */
    BugResponse getBugByNumber(String bugNumber);

    /**
     * Get all active bugs
     *
     * @return List of all bugs
     */
    List<BugResponse> getAllBugs();

    /**
     * Get bugs reported by a specific user
     *
     * @param reporterId Reporter's UUID
     * @return List of bugs
     */
    List<BugResponse> getBugsByReporter(UUID reporterId);

    /**
     * Get bugs assigned to a specific developer
     *
     * @param assignedTo Developer's UUID
     * @return List of bugs
     */
    List<BugResponse> getBugsByAssignedDeveloper(UUID assignedTo);

    /**
     * Get bugs by status
     *
     * @param status Bug status
     * @return List of bugs
     */
    List<BugResponse> getBugsByStatus(BugStatus status);

    /**
     * Get bugs by priority
     *
     * @param priority Bug priority
     * @return List of bugs
     */
    List<BugResponse> getBugsByPriority(BugPriority priority);

    /**
     * Get bugs by severity
     *
     * @param severity Bug severity
     * @return List of bugs
     */
    List<BugResponse> getBugsBySeverity(BugSeverity severity);

    /**
     * Get bugs by module/component
     *
     * @param module Module name
     * @return List of bugs
     */
    List<BugResponse> getBugsByModule(String module);

    /**
     * Get all unassigned bugs
     *
     * @return List of unassigned bugs
     */
    List<BugResponse> getUnassignedBugs();

    /**
     * Search bugs by keyword in title or description
     *
     * @param keyword Search term
     * @return List of matching bugs
     */
    List<BugResponse> searchBugs(String keyword);

    /**
     * Assign bug to a developer
     * Updates assignedTo field and changes status to IN_PROGRESS if currently OPEN
     *
     * @param bugId Bug UUID
     * @param developerId Developer's UUID
     * @return Updated bug
     * @throws ResourceNotFoundException if bug doesn't exist
     */
    BugResponse assignBug(UUID bugId, UUID developerId);

    /**
     * Change bug status
     * Automatically updates resolvedAt/closedAt timestamps based on new status
     *
     * @param bugId Bug UUID
     * @param newStatus New status
     * @return Updated bug
     * @throws ResourceNotFoundException if bug doesn't exist
     */
    BugResponse changeStatus(UUID bugId, BugStatus newStatus);

    /**
     * Soft delete a bug (sets active = false)
     * Does not actually remove from database
     *
     * @param bugId Bug UUID
     * @throws ResourceNotFoundException if bug doesn't exist
     */
    void deleteBug(UUID bugId);

    /**
     * Get count of bugs by status
     *
     * @param status Bug status
     * @return Count
     */
    long countBugsByStatus(BugStatus status);

    /**
     * Get count of bugs by priority
     *
     * @param priority Bug priority
     * @return Count
     */
    long countBugsByPriority(BugPriority priority);

    /**
     * Get count of bugs assigned to a developer
     *
     * @param developerId Developer's UUID
     * @return Count
     */
    long countBugsByDeveloper(UUID developerId);
}
