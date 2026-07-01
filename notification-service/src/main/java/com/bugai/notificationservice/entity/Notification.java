package com.bugai.notificationservice.entity;

import com.bugai.notificationservice.enums.NotificationChannel;
import com.bugai.notificationservice.enums.NotificationStatus;
import com.bugai.notificationservice.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    // Unique identifier for the notification
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // UUID of the user receiving the notification
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    // UUID of the bug this notification is about (can be null for non-bug notifications)
    @Column(columnDefinition = "BINARY(16)")
    private UUID bugId;

    // Type of notification event (e.g., BUG_ASSIGNED, BUG_RESOLVED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // Channel through which the notification is sent (EMAIL, IN_APP, SMS, SLACK)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    // Current status of the notification (PENDING, SENT, FAILED, DELIVERED, READ)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    // Subject line (mainly for EMAIL channel)
    @Column(nullable = false, length = 255)
    private String subject;

    // Detailed message content
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // Recipient email/phone/slack handle (extracted from user context in Phase 2 via JWT)
    @Column(length = 255)
    private String recipient;

    // Error message if notification failed to send
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    // Number of retry attempts
    @Builder.Default
    private Integer retryCount = 0;

    // Timestamp when notification was created
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Timestamp when notification was last updated
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Timestamp when notification was sent (null if not yet sent)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        // Generate UUID if not already set
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        // Set default status to PENDING if not specified
        if (this.status == null) {
            this.status = NotificationStatus.PENDING;
        }
        // Initialize retry count to 0
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }
}