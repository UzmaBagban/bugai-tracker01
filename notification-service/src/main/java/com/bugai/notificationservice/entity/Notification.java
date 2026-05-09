package com.bugai.notificationservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Notification entity representing a notification sent to a user.
 *
 * Notifications can be of various types (BUG_ASSIGNED, BUG_UPDATED, etc.) and
 * delivered through different channels (EMAIL, SMS, IN_APP).
 *
 * Tracks delivery status and retry attempts for reliability.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_recipient_id", columnList = "recipientId"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    /**
     * Primary key - auto-generated Long ID for database efficiency.
     * Using Long instead of UUID for faster indexing and foreign key relationships.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * UUID of the user receiving this notification.
     * References the User entity in the User Service.
     */
    @Column(nullable = false, length = 36)
    private String recipientId;

    /**
     * Type of notification (e.g., BUG_ASSIGNED, BUG_RESOLVED).
     * Determines the notification template and urgency.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    /**
     * Delivery channel for this notification (EMAIL, SMS, IN_APP).
     * A single notification event may create multiple Notification records
     * for different channels.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    /**
     * Subject line for email notifications or title for in-app notifications.
     */
    @Column(nullable = false, length = 200)
    private String subject;

    /**
     * Main content of the notification.
     * Can contain plain text or HTML depending on the channel.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Current delivery status of the notification.
     * Transitions: PENDING -> SENT -> DELIVERED (or FAILED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    /**
     * Number of delivery attempts made for this notification.
     * Used for retry logic and exponential backoff.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    /**
     * Timestamp when the notification was created.
     * Automatically set by Hibernate on insert.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to this notification.
     * Automatically updated by Hibernate on any modification.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Timestamp when the notification was successfully sent.
     * Null if still pending or failed.
     */
    private LocalDateTime sentAt;

    /**
     * Error message if the notification delivery failed.
     * Used for debugging and display in admin dashboards.
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Optional reference to the related entity (e.g., bugId).
     * Allows users to navigate to the source of the notification.
     */
    @Column(length = 36)
    private String relatedEntityId;

    /**
     * Type of the related entity (e.g., BUG, COMMENT, USER).
     * Used in combination with relatedEntityId for deep linking.
     */
    @Column(length = 50)
    private String relatedEntityType;

    /**
     * Whether the notification has been read by the recipient.
     * Only applicable for IN_APP channel; always false for EMAIL/SMS.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean read = false;

    /**
     * Timestamp when the notification was marked as read.
     * Null if not yet read.
     */
    private LocalDateTime readAt;
}