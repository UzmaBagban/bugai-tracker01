package com.bugai.bugservice.enums;


/**
 * BugStatus enum - represents the lifecycle state of a bug
 *
 * Workflow:
 * OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED
 *         ↓
 *    REOPENED (back to IN_PROGRESS or RESOLVED)
 */
public enum BugStatus {
    /**
     * Bug has been reported but not yet assigned or started
     */
    OPEN,

    /**
     * Developer is actively working on the bug
     */
    IN_PROGRESS,

    /**
     * Bug has been fixed and deployed, awaiting verification
     */
    RESOLVED,

    /**
     * Bug has been verified as fixed and ticket is closed
     */
    CLOSED,

    /**
     * Bug was closed but has reappeared or was incorrectly marked resolved
     */
    REOPENED,

    /**
     * Bug is blocked by external dependencies or other bugs
     */
    BLOCKED,

    /**
     * Bug cannot be reproduced or lacks sufficient information
     */
    CANNOT_REPRODUCE,

    /**
     * Bug is rejected as not being an actual issue (working as designed)
     */
    REJECTED
}