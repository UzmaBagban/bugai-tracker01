package com.bugai.bugservice.controller;


import com.bugai.bugservice.dto.*;
import com.bugai.bugservice.enums.*;
import com.bugai.bugservice.repository.BugRepository;
import com.bugai.bugservice.exception.*;
import com.bugai.bugservice.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * BugController - REST API endpoints for bug management
 * Base path: /api/bugs
 * Uses constructor injection via @RequiredArgsConstructor
 * All responses wrapped in ResponseEntity with appropriate HTTP status codes
 */
@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
@Slf4j
public class BugController {

    /**
     * Service dependency - injected via constructor
     */
    private final BugService bugService;

    /**
     * CREATE: Create a new bug
     * POST /api/bugs
     * Returns 201 Created with bug details and Location header
     */
    @PostMapping
    public ResponseEntity<BugResponse> createBug(@Valid @RequestBody BugRequest request) {
        log.info("REST: Creating new bug");

        BugResponse response = bugService.createBug(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * UPDATE: Update an existing bug
     * PUT /api/bugs/{bugId}
     * Returns 200 OK with updated bug details
     */
    @PutMapping("/{bugId}")
    public ResponseEntity<BugResponse> updateBug(
            @PathVariable UUID bugId,
            @Valid @RequestBody BugRequest request) {

        log.info("REST: Updating bug: {}", bugId);

        BugResponse response = bugService.updateBug(bugId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bug by ID
     * GET /api/bugs/{bugId}
     * Returns 200 OK with bug details
     */
    @GetMapping("/{bugId}")
    public ResponseEntity<BugResponse> getBugById(@PathVariable UUID bugId) {
        log.info("REST: Fetching bug by ID: {}", bugId);

        BugResponse response = bugService.getBugById(bugId);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bug by bug number
     * GET /api/bugs/number/{bugNumber}
     * Returns 200 OK with bug details
     */
    @GetMapping("/number/{bugNumber}")
    public ResponseEntity<BugResponse> getBugByNumber(@PathVariable String bugNumber) {
        log.info("REST: Fetching bug by number: {}", bugNumber);

        BugResponse response = bugService.getBugByNumber(bugNumber);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get all active bugs
     * GET /api/bugs
     * Returns 200 OK with list of bugs
     */
    @GetMapping
    public ResponseEntity<List<BugResponse>> getAllBugs() {
        log.info("REST: Fetching all bugs");

        List<BugResponse> response = bugService.getAllBugs();

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bugs by reporter
     * GET /api/bugs/reporter/{reporterId}
     * Returns 200 OK with list of bugs
     */
    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<List<BugResponse>> getBugsByReporter(@PathVariable UUID reporterId) {
        log.info("REST: Fetching bugs for reporter: {}", reporterId);

        List<BugResponse> response = bugService.getBugsByReporter(reporterId);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bugs by assigned developer
     * GET /api/bugs/assigned/{developerId}
     * Returns 200 OK with list of bugs
     */
    @GetMapping("/assigned/{developerId}")
    public ResponseEntity<List<BugResponse>> getBugsByAssignedDeveloper(
            @PathVariable UUID developerId) {

        log.info("REST: Fetching bugs assigned to: {}", developerId);

        List<BugResponse> response = bugService.getBugsByAssignedDeveloper(developerId);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bugs by status
     * GET /api/bugs/status/{status}
     * Returns 200 OK with list of bugs
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BugResponse>> getBugsByStatus(@PathVariable BugStatus status) {
        log.info("REST: Fetching bugs with status: {}", status);

        List<BugResponse> response = bugService.getBugsByStatus(status);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bugs by priority
     * GET /api/bugs/priority/{priority}
     * Returns 200 OK with list of bugs
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<BugResponse>> getBugsByPriority(@PathVariable BugPriority priority) {
        log.info("REST: Fetching bugs with priority: {}", priority);

        List<BugResponse> response = bugService.getBugsByPriority(priority);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bugs by severity
     * GET /api/bugs/severity/{severity}
     * Returns 200 OK with list of bugs
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<BugResponse>> getBugsBySeverity(@PathVariable BugSeverity severity) {
        log.info("REST: Fetching bugs with severity: {}", severity);

        List<BugResponse> response = bugService.getBugsBySeverity(severity);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get bugs by module
     * GET /api/bugs/module/{module}
     * Returns 200 OK with list of bugs
     */
    @GetMapping("/module/{module}")
    public ResponseEntity<List<BugResponse>> getBugsByModule(@PathVariable String module) {
        log.info("REST: Fetching bugs for module: {}", module);

        List<BugResponse> response = bugService.getBugsByModule(module);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get unassigned bugs
     * GET /api/bugs/unassigned
     * Returns 200 OK with list of bugs
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<BugResponse>> getUnassignedBugs() {
        log.info("REST: Fetching unassigned bugs");

        List<BugResponse> response = bugService.getUnassignedBugs();

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Search bugs by keyword
     * GET /api/bugs/search?keyword=xyz
     * Returns 200 OK with list of matching bugs
     */
    @GetMapping("/search")
    public ResponseEntity<List<BugResponse>> searchBugs(@RequestParam String keyword) {
        log.info("REST: Searching bugs with keyword: {}", keyword);

        List<BugResponse> response = bugService.searchBugs(keyword);

        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE: Assign bug to developer
     * PATCH /api/bugs/{bugId}/assign
     * Request body: { "developerId": "uuid-here" }
     * Returns 200 OK with updated bug
     */
    @PatchMapping("/{bugId}/assign")
    public ResponseEntity<BugResponse> assignBug(
            @PathVariable UUID bugId,
            @RequestBody Map<String, UUID> body) {

        UUID developerId = body.get("developerId");
        log.info("REST: Assigning bug {} to developer {}", bugId, developerId);

        BugResponse response = bugService.assignBug(bugId, developerId);

        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE: Change bug status
     * PATCH /api/bugs/{bugId}/status
     * Request body: { "status": "RESOLVED" }
     * Returns 200 OK with updated bug
     */
    @PatchMapping("/{bugId}/status")
    public ResponseEntity<BugResponse> changeStatus(
            @PathVariable UUID bugId,
            @RequestBody Map<String, BugStatus> body) {

        BugStatus newStatus = body.get("status");
        log.info("REST: Changing bug {} status to {}", bugId, newStatus);

        BugResponse response = bugService.changeStatus(bugId, newStatus);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE: Soft delete a bug
     * DELETE /api/bugs/{bugId}
     * Returns 204 No Content
     */
    @DeleteMapping("/{bugId}")
    public ResponseEntity<Void> deleteBug(@PathVariable UUID bugId) {
        log.info("REST: Deleting bug: {}", bugId);

        bugService.deleteBug(bugId);

        return ResponseEntity.noContent().build();
    }

    /**
     * READ: Get bug count by status
     * GET /api/bugs/count/status/{status}
     * Returns 200 OK with count
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Long>> countByStatus(@PathVariable BugStatus status) {
        log.info("REST: Counting bugs with status: {}", status);

        long count = bugService.countBugsByStatus(status);

        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * READ: Get bug count by priority
     * GET /api/bugs/count/priority/{priority}
     * Returns 200 OK with count
     */
    @GetMapping("/count/priority/{priority}")
    public ResponseEntity<Map<String, Long>> countByPriority(@PathVariable BugPriority priority) {
        log.info("REST: Counting bugs with priority: {}", priority);

        long count = bugService.countBugsByPriority(priority);

        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * READ: Get bug count by developer
     * GET /api/bugs/count/developer/{developerId}
     * Returns 200 OK with count
     */
    @GetMapping("/count/developer/{developerId}")
    public ResponseEntity<Map<String, Long>> countByDeveloper(@PathVariable UUID developerId) {
        log.info("REST: Counting bugs for developer: {}", developerId);

        long count = bugService.countBugsByDeveloper(developerId);

        return ResponseEntity.ok(Map.of("count", count));
    }
}
