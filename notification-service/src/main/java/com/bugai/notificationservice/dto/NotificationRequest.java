package com.bugai.notificationservice.dto;


import com.bugai.notificationservice.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new notification.
 *
 * Used by other microservices (Bug Service, User Service) to request
 * that a notification be sent to a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    /**
     * UUID of the user who will receive the notification.
     * Must match a valid user in the User Service.
     */
    @NotBlank(message = "Recipient ID is required")
    @Size(max = 36, message = "Recipient ID must be a valid UUID")
    private String recipientId;

    /**
     * Type of notification being sent.
     * Determines the template and formatting used.
     */
    @NotNull(message = "Notification type is required")
    private NotificationType type;

    /**
     * Delivery channel for this notification.
     * Multiple notifications can be created for different channels.
     */
    @NotNull(message = "Notification channel is required")
    private NotificationChannel channel;

    /**
     * Subject line for the notification.
     * Used as email subject or in-app notification title.
     */
    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    /**
     * Main content/body of the notification.
     * Can contain plain text or HTML depending on channel.
     */
    @NotBlank(message = "Content is required")
    private String content;

    /**
     * Optional ID of the related entity that triggered this notification.
     * E.g., the bugId for BUG_ASSIGNED notifications.
     */
    @Size(max = 36, message = "Related entity ID must be a valid UUID")
    private String relatedEntityId;

    /**
     * Optional type of the related entity (e.g., "BUG", "COMMENT").
     * Used for deep linking in the UI.
     */
    @Size(max = 50, message = "Related entity type must not exceed 50 characters")
    private String relatedEntityType;
}