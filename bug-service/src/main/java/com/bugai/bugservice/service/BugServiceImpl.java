package com.bugai.bugservice.service;



import com.bugai.bugservice.dto.*;
import com.bugai.bugservice.enums.*;
import com.bugai.bugservice.repository.*;
import com.bugai.bugservice.entity.*;
import com.bugai.bugservice.exception.*;
import com.bugai.bugservice.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BugServiceImpl - implements BugService interface
 * Contains all business logic for bug management
 * Uses constructor injection via @RequiredArgsConstructor
 * All write operations are @Transactional for atomicity
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BugServiceImpl implements BugService {

    /**
     * Repository dependency - injected via constructor
     */
    private final BugRepository bugRepository;

    /**
     * Creates a new bug with auto-generated bug number
     * Applies default values for optional fields
     */
    @Override
    @Transactional
    public BugResponse createBug(BugRequest request) {
        log.info("Creating new bug with title: {}", request.getTitle());

        // Generate sequential bug number (BUG-1001, BUG-1002, etc.)
        String bugNumber = generateBugNumber();

        // Build entity from request DTO
        // Use @Builder pattern with defaults from entity class
        Bug bug = Bug.builder()
                .bugNumber(bugNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : BugStatus.OPEN)
                .priority(request.getPriority() != null ? request.getPriority() : BugPriority.MEDIUM)
                .severity(request.getSeverity() != null ? request.getSeverity() : BugSeverity.MAJOR)
                .reporterId(request.getReporterId())
                .assignedTo(request.getAssignedTo())
                .module(request.getModule())
                .environment(request.getEnvironment())
                .stepsToReproduce(request.getStepsToReproduce())
                .expectedBehavior(request.getExpectedBehavior())
                .actualBehavior(request.getActualBehavior())
                .tags(request.getTags())
                .active(true)
                .build();

        // Save to database - generates UUID and timestamps
        Bug savedBug = bugRepository.save(bug);

        log.info("Bug created successfully with ID: {} and number: {}",
                savedBug.getBugId(), savedBug.getBugNumber());

        // Convert entity to response DTO before returning
        return toResponse(savedBug);
    }

    /**
     * Updates an existing bug
     * Cannot update: bugId, bugNumber, reporterId, timestamps
     * Auto-updates resolvedAt/closedAt based on status changes
     */
    @Override
    @Transactional
    public BugResponse updateBug(UUID bugId, BugRequest request) {
        log.info("Updating bug with ID: {}", bugId);

        // Fetch existing bug - throws exception if not found
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with ID: " + bugId));

        // Update fields from request
        // Only update non-null fields to support partial updates
        if (request.getTitle() != null) {
            bug.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            bug.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            updateStatusTimestamps(bug, request.getStatus());
            bug.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            bug.setPriority(request.getPriority());
        }
        if (request.getSeverity() != null) {
            bug.setSeverity(request.getSeverity());
        }
        if (request.getAssignedTo() != null) {
            bug.setAssignedTo(request.getAssignedTo());
        }
        if (request.getModule() != null) {
            bug.setModule(request.getModule());
        }
        if (request.getEnvironment() != null) {
            bug.setEnvironment(request.getEnvironment());
        }
        if (request.getStepsToReproduce() != null) {
            bug.setStepsToReproduce(request.getStepsToReproduce());
        }
        if (request.getExpectedBehavior() != null) {
            bug.setExpectedBehavior(request.getExpectedBehavior());
        }
        if (request.getActualBehavior() != null) {
            bug.setActualBehavior(request.getActualBehavior());
        }
        if (request.getTags() != null) {
            bug.setTags(request.getTags());
        }

        // Save updates - @UpdateTimestamp handles updatedAt automatically
        Bug updatedBug = bugRepository.save(bug);

        log.info("Bug updated successfully: {}", bugId);

        return toResponse(updatedBug);
    }

    /**
     * Retrieves bug by UUID
     */
    @Override
    public BugResponse getBugById(UUID bugId) {
        log.info("Fetching bug by ID: {}", bugId);

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with ID: " + bugId));

        return toResponse(bug);
    }

    /**
     * Retrieves bug by human-readable bug number
     */
    @Override
    public BugResponse getBugByNumber(String bugNumber) {
        log.info("Fetching bug by number: {}", bugNumber);

        Bug bug = bugRepository.findByBugNumberAndActiveTrue(bugNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with number: " + bugNumber));

        return toResponse(bug);
    }

    /**
     * Retrieves all active bugs
     */
    @Override
    public List<BugResponse> getAllBugs() {
        log.info("Fetching all active bugs");

        List<Bug> bugs = bugRepository.findByActiveTrue();

        // Convert list of entities to list of DTOs using stream
        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets bugs reported by specific user
     */
    @Override
    public List<BugResponse> getBugsByReporter(UUID reporterId) {
        log.info("Fetching bugs for reporter: {}", reporterId);

        List<Bug> bugs = bugRepository.findByReporterIdAndActiveTrue(reporterId);

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets bugs assigned to specific developer
     */
    @Override
    public List<BugResponse> getBugsByAssignedDeveloper(UUID assignedTo) {
        log.info("Fetching bugs assigned to: {}", assignedTo);

        List<Bug> bugs = bugRepository.findByAssignedToAndActiveTrue(assignedTo);

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets bugs by status
     */
    @Override
    public List<BugResponse> getBugsByStatus(BugStatus status) {
        log.info("Fetching bugs with status: {}", status);

        List<Bug> bugs = bugRepository.findByStatusAndActiveTrue(status);

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets bugs by priority
     */
    @Override
    public List<BugResponse> getBugsByPriority(BugPriority priority) {
        log.info("Fetching bugs with priority: {}", priority);

        List<Bug> bugs = bugRepository.findByPriorityAndActiveTrue(priority);

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets bugs by severity
     */
    @Override
    public List<BugResponse> getBugsBySeverity(BugSeverity severity) {
        log.info("Fetching bugs with severity: {}", severity);

        List<Bug> bugs = bugRepository.findBySeverityAndActiveTrue(severity);

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets bugs by module
     */
    @Override
    public List<BugResponse> getBugsByModule(String module) {
        log.info("Fetching bugs for module: {}", module);

        List<Bug> bugs = bugRepository.findByModuleAndActiveTrue(module);

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets all unassigned bugs
     */
    @Override
    public List<BugResponse> getUnassignedBugs() {
        log.info("Fetching unassigned bugs");

        List<Bug> bugs = bugRepository.findByAssignedToIsNullAndActiveTrue();

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Searches bugs by keyword in title or description
     */
    @Override
    public List<BugResponse> searchBugs(String keyword) {
        log.info("Searching bugs with keyword: {}", keyword);

        List<Bug> bugs = bugRepository.searchByKeyword(keyword);

        return bugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Assigns bug to a developer
     * Changes status to IN_PROGRESS if currently OPEN
     */
    @Override
    @Transactional
    public BugResponse assignBug(UUID bugId, UUID developerId) {
        log.info("Assigning bug {} to developer {}", bugId, developerId);

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with ID: " + bugId));

        // Update assignment
        bug.setAssignedTo(developerId);

        // If bug is OPEN, move to IN_PROGRESS when assigned
        if (bug.getStatus() == BugStatus.OPEN) {
            bug.setStatus(BugStatus.IN_PROGRESS);
            log.info("Bug status changed from OPEN to IN_PROGRESS");
        }

        Bug updatedBug = bugRepository.save(bug);

        log.info("Bug assigned successfully");

        return toResponse(updatedBug);
    }

    /**
     * Changes bug status
     * Updates resolvedAt/closedAt timestamps automatically
     */
    @Override
    @Transactional
    public BugResponse changeStatus(UUID bugId, BugStatus newStatus) {
        log.info("Changing status of bug {} to {}", bugId, newStatus);

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with ID: " + bugId));

        // Update status and related timestamps
        updateStatusTimestamps(bug, newStatus);
        bug.setStatus(newStatus);

        Bug updatedBug = bugRepository.save(bug);

        log.info("Bug status changed successfully");

        return toResponse(updatedBug);
    }

    /**
     * Soft deletes a bug (sets active = false)
     */
    @Override
    @Transactional
    public void deleteBug(UUID bugId) {
        log.info("Soft deleting bug: {}", bugId);

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with ID: " + bugId));

        // Soft delete - keep in database but mark as inactive
        bug.setActive(false);
        bugRepository.save(bug);

        log.info("Bug soft deleted successfully");
    }

    /**
     * Counts bugs by status
     */
    @Override
    public long countBugsByStatus(BugStatus status) {
        return bugRepository.countByStatusAndActiveTrue(status);
    }

    /**
     * Counts bugs by priority
     */
    @Override
    public long countBugsByPriority(BugPriority priority) {
        return bugRepository.countByPriorityAndActiveTrue(priority);
    }

    /**
     * Counts bugs assigned to developer
     */
    @Override
    public long countBugsByDeveloper(UUID developerId) {
        return bugRepository.countByAssignedToAndActiveTrue(developerId);
    }

    /**
     * PRIVATE HELPER: Generates next sequential bug number
     * Format: BUG-1001, BUG-1002, etc.
     */
    private String generateBugNumber() {
        // Get highest existing bug number
        String maxBugNumber = bugRepository.findMaxBugNumber();

        // If no bugs exist yet, start with BUG-1001
        if (maxBugNumber == null) {
            return "BUG-1001";
        }

        // Extract numeric part and increment
        // Example: "BUG-1001" -> extract "1001" -> increment to 1002
        String numericPart = maxBugNumber.replace("BUG-", "");
        int nextNumber = Integer.parseInt(numericPart) + 1;

        // Format with leading zeros (4 digits)
        return String.format("BUG-%04d", nextNumber);
    }

    /**
     * PRIVATE HELPER: Updates resolvedAt/closedAt timestamps based on status change
     * - RESOLVED status: sets resolvedAt
     * - CLOSED status: sets closedAt
     * - REOPENED status: clears both timestamps
     */
    private void updateStatusTimestamps(Bug bug, BugStatus newStatus) {
        if (newStatus == BugStatus.RESOLVED) {
            bug.setResolvedAt(LocalDateTime.now());
        } else if (newStatus == BugStatus.CLOSED) {
            bug.setClosedAt(LocalDateTime.now());
        } else if (newStatus == BugStatus.REOPENED) {
            // Clear timestamps when bug is reopened
            bug.setResolvedAt(null);
            bug.setClosedAt(null);
        }
    }

    /**
     * PRIVATE HELPER: Converts Bug entity to BugResponse DTO
     * Never expose raw entity to controller layer
     */
    private BugResponse toResponse(Bug bug) {
        return BugResponse.builder()
                .bugId(bug.getBugId())
                .bugNumber(bug.getBugNumber())
                .title(bug.getTitle())
                .description(bug.getDescription())
                .status(bug.getStatus())
                .priority(bug.getPriority())
                .severity(bug.getSeverity())
                .reporterId(bug.getReporterId())
                .assignedTo(bug.getAssignedTo())
                .module(bug.getModule())
                .environment(bug.getEnvironment())
                .stepsToReproduce(bug.getStepsToReproduce())
                .expectedBehavior(bug.getExpectedBehavior())
                .actualBehavior(bug.getActualBehavior())
                .tags(bug.getTags())
                .createdAt(bug.getCreatedAt())
                .updatedAt(bug.getUpdatedAt())
                .resolvedAt(bug.getResolvedAt())
                .closedAt(bug.getClosedAt())
                .active(bug.getActive())
                .build();
    }
}