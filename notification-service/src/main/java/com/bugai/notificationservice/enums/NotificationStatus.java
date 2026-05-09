package com.bugai.notificationservice.enums;


/**
 * Enum representing the current status of a notification's delivery lifecycle.
 *
 * Notifications transition through these states during delivery and retry processes.
 */
public enum NotificationStatus {

    /**
     * Notification has been created but not yet sent.
     * Initial state for all new notifications.
     */
    PENDING,

    /**
     * Notification has been submitted to the delivery service (email/SMS provider).
     * Waiting for confirmation of delivery.
     */
    SENT,

    /**
     * Notification has been successfully delivered to the recipient.
     * Confirmed by the delivery service provider.
     */
    DELIVERED,

    /**
     * Notification delivery failed after all retry attempts.
     * Check errorMessage field for failure details.
     */
    FAILED,

    /**
     * Notification has been cancelled before delivery.
     * May occur if the triggering event is reverted or user opts out.
     */
    CANCELLED
}