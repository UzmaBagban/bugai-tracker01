package com.bugai.bugservice.enums;


/**
 * BugPriority enum - indicates how urgently the bug needs to be fixed
 * Based on business impact and time constraints
 */
public enum BugPriority {
    /**
     * Low urgency - can be fixed in future sprints
     */
    LOW,

    /**
     * Normal priority - should be fixed in upcoming sprint
     */
    MEDIUM,

    /**
     * High priority - needs to be fixed soon (within current sprint)
     */
    HIGH,

    /**
     * Critical - must be fixed immediately (production down, data loss, etc.)
     */
    CRITICAL
}
