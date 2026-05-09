package com.bugai.notificationservice.dto;



import com.bugai.notificationservice.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Notification entity.
 *
 * Returned by all notification API endpoints.
 * Never exposes internal database IDs or sensitive error details to external services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    /**
     * Database ID of the notification.
     * Used for update and delete operations.
     */
    private Long id;

    /**
     * UUID of the recipient user.
     */
    private String recipientId;

    /**
     * Type of notification.
     */
    private NotificationType type;

    /**
     * Delivery channel used.
     */
    private NotificationChannel channel;

    /**
     * Subject/title of the notification.
     */
    private String subject;

    /**
     * Body content of the notification.
     */
    private String content;

    /**
     * Current delivery status.
     */
    private NotificationStatus status;

    /**
     * Number of delivery attempts made.
     */
    private Integer retryCount;

    /**
     * When the notification was created.
     */
    private LocalDateTime createdAt;

    /**
     * When the notification was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * When the notification was successfully sent (if applicable).
     */
    private LocalDateTime sentAt;

    /**
     * Error message if delivery failed.
     */
    private String errorMessage;

    /**
     * ID of the related entity (e.g., bugId).
     */
    private String relatedEntityId;

    /**
     * Type of the related entity (e.g., "BUG").
     */
    private String relatedEntityType;

    /**
     * Whether the notification has been read (IN_APP only).
     */
    private Boolean read;

    /**
     * When the notification was marked as read (if applicable).
     */
    private LocalDateTime readAt;
}