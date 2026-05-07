package com.bugai.bugservice.enums;


/**
 * BugSeverity enum - indicates the technical impact of the bug
 * Based on how much functionality is affected
 */
public enum BugSeverity {
    /**
     * Minor issue - cosmetic problems, minor usability issues
     */
    MINOR,

    /**
     * Major issue - significant functionality affected but workarounds exist
     */
    MAJOR,

    /**
     * Critical issue - major functionality broken, no workaround
     */
    CRITICAL,

    /**
     * Blocker - system completely unusable, prevents all work
     */
    BLOCKER
}