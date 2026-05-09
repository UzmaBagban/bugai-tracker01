package com.bugai.notificationservice.repository;


import com.bugai.notificationservice.entity.Notification;
import com.bugai.notificationservice.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity.
 *
 * Provides CRUD operations and custom queries for notification management,
 * retrieval by user, status filtering, and retry handling.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a specific recipient.
     * Ordered by creation date descending (newest first).
     *
     * Used for fetching a user's notification history.
     */
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    /**
     * Find all unread in-app notifications for a specific recipient.
     * Only applicable for IN_APP channel.
     *
     * Used for displaying the notification badge count in the UI.
     */
    List<Notification> findByRecipientIdAndChannelAndReadFalseOrderByCreatedAtDesc(
            String recipientId,
            NotificationChannel channel
    );

    /**
     * Find all notifications with a specific status.
     *
     * Used for background jobs that process PENDING or FAILED notifications.
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Find all notifications of a specific type for a recipient.
     *
     * Useful for analytics or filtering notifications by category.
     */
    List<Notification> findByRecipientIdAndType(String recipientId, NotificationType type);

    /**
     * Find all pending notifications that have exceeded retry count threshold.
     *
     * Custom query to identify notifications that have failed multiple times
     * and should be moved to FAILED status.
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.retryCount >= :maxRetries")
    List<Notification> findByStatusAndRetryCountGreaterThanEqual(
            @Param("status") NotificationStatus status,
            @Param("maxRetries") Integer maxRetries
    );

    /**
     * Find all pending notifications older than a specific time.
     *
     * Used to identify stuck notifications that need retry or cleanup.
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.createdAt < :cutoffTime")
    List<Notification> findStuckNotifications(
            @Param("status") NotificationStatus status,
            @Param("cutoffTime") LocalDateTime cutoffTime
    );

    /**
     * Find all notifications related to a specific entity.
     *
     * E.g., find all notifications related to a particular bug.
     */
    List<Notification> findByRelatedEntityIdAndRelatedEntityType(
            String relatedEntityId,
            String relatedEntityType
    );

    /**
     * Count unread in-app notifications for a user.
     *
     * Used for displaying notification badge count in the UI.
     */
    Long countByRecipientIdAndChannelAndReadFalse(
            String recipientId,
            NotificationChannel channel
    );

    /**
     * Find notifications by recipient, channel, and status.
     *
     * Useful for debugging delivery issues or generating reports.
     */
    List<Notification> findByRecipientIdAndChannelAndStatus(
            String recipientId,
            NotificationChannel channel,
            NotificationStatus status
    );
}