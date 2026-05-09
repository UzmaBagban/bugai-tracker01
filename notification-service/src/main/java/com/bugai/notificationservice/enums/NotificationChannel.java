package com.bugai.notificationservice.enums;


/**
 * Enum representing the delivery channels for notifications.
 *
 * A single notification event can be sent through multiple channels
 * based on user preferences and notification type.
 */
public enum NotificationChannel {

    /**
     * Email notification sent to the user's registered email address.
     * Uses SMTP or email service provider (e.g., SendGrid, AWS SES).
     */
    EMAIL,

    /**
     * SMS notification sent to the user's registered phone number.
     * Requires integration with SMS provider (e.g., Twilio, AWS SNS).
     * Typically used for high-priority or urgent notifications.
     */
    SMS,

    /**
     * In-application notification displayed in the BugAI Tracker UI.
     * Stored in the database and retrieved via API calls.
     * Supports real-time push via WebSocket (future enhancement).
     */
    IN_APP
}
