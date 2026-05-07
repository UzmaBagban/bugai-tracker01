package com.bugai.bugservice.repository;


import com.bugai.bugservice.entity.Bug;
import com.bugai.bugservice.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BugRepository - Data access layer for Bug entity
 * Extends JpaRepository to get CRUD methods for free
 * Custom query methods follow Spring Data JPA naming conventions
 */
@Repository
public interface BugRepository extends JpaRepository<Bug, UUID> {

    /**
     * Find bug by its human-readable bug number (e.g., BUG-1001)
     * Only returns active bugs
     */
    Optional<Bug> findByBugNumberAndActiveTrue(String bugNumber);

    /**
     * Find all active bugs (soft delete support)
     */
    List<Bug> findByActiveTrue();

    /**
     * Find bugs by reporter UUID
     * Useful for "my reported bugs" feature
     */
    List<Bug> findByReporterIdAndActiveTrue(UUID reporterId);

    /**
     * Find bugs assigned to a specific developer
     * Useful for "my assigned bugs" feature
     */
    List<Bug> findByAssignedToAndActiveTrue(UUID assignedTo);

    /**
     * Find bugs by status
     * Useful for dashboard filters
     */
    List<Bug> findByStatusAndActiveTrue(BugStatus status);

    /**
     * Find bugs by priority
     */
    List<Bug> findByPriorityAndActiveTrue(BugPriority priority);

    /**
     * Find bugs by severity
     */
    List<Bug> findBySeverityAndActiveTrue(BugSeverity severity);

    /**
     * Find bugs by module/component
     */
    List<Bug> findByModuleAndActiveTrue(String module);

    /**
     * Find unassigned bugs
     * Useful for assignment workflows
     */
    List<Bug> findByAssignedToIsNullAndActiveTrue();

    /**
     * Find highest bug number to generate next sequential number
     * Uses native query to extract numeric part from bug number
     */
    @Query("SELECT MAX(b.bugNumber) FROM Bug b")
    String findMaxBugNumber();

    /**
     * Search bugs by title or description (case-insensitive)
     * Useful for search functionality
     */
    @Query("SELECT b FROM Bug b WHERE b.active = true AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Bug> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Count bugs by status
     * Useful for dashboard statistics
     */
    long countByStatusAndActiveTrue(BugStatus status);

    /**
     * Count bugs by priority
     */
    long countByPriorityAndActiveTrue(BugPriority priority);

    /**
     * Count bugs assigned to a developer
     */
    long countByAssignedToAndActiveTrue(UUID assignedTo);
}