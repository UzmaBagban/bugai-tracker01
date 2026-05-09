package com.bugai.notificationservice.enums;


/**
 * Enum representing all types of notifications in the BugAI Tracker system.
 *
 * Each type corresponds to a specific event that triggers a notification,
 * and maps to a specific notification template.
 */
public enum NotificationType {

    /**
     * Sent when a bug is newly assigned to a developer.
     */
    BUG_ASSIGNED,

    /**
     * Sent when a bug is reassigned to a different developer.
     */
    BUG_REASSIGNED,

    /**
     * Sent when any field of a bug is updated (status, priority, description, etc.).
     */
    BUG_UPDATED,

    /**
     * Sent when a bug status changes to RESOLVED.
     */
    BUG_RESOLVED,

    /**
     * Sent when a bug status changes to CLOSED.
     */
    BUG_CLOSED,

    /**
     * Sent when a bug status changes back to OPEN after being resolved/closed.
     */
    BUG_REOPENED,

    /**
     * Sent when a comment is added to a bug.
     */
    COMMENT_ADDED,

    /**
     * Sent when a user is mentioned in a bug description or comment using @username.
     */
    MENTION,

    /**
     * Sent when a bug's priority is escalated (e.g., MEDIUM -> HIGH).
     */
    PRIORITY_ESCALATED,

    /**
     * Sent when a bug's SLA is approaching its deadline.
     * (Phase 2 feature)
     */
    SLA_WARNING,

    /**
     * Sent when a bug's SLA deadline has been breached.
     * (Phase 2 feature)
     */
    SLA_BREACH,

    /**
     * System notification for account-related events.
     */
    ACCOUNT_UPDATE,

    /**
     * General system notification (maintenance, announcements, etc.).
     */
    SYSTEM_NOTIFICATION
}
