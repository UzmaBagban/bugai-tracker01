package com.bugai.notificationservice.service;


import com.bugai.notificationservice.dto.*;
import com.bugai.notification.model.NotificationChannel;
import com.bugai.notification.model.NotificationStatus;

import java.util.List;

/**
 * Service interface for notification management operations.
 *
 * Defines the contract for creating, retrieving, updating, and delivering notifications.
 */
public interface NotificationService {

    /**
     * Create a new notification.
     *
     * @param request The notification details
     * @return The created notification
     */
    NotificationResponse createNotification(NotificationRequest request);

    /**
     * Get a notification by its ID.
     *
     * @param id The notification ID
     * @return The notification details
     */
    NotificationResponse getNotificationById(Long id);

    /**
     * Get all notifications for a specific recipient.
     *
     * @param recipientId The user's UUID
     * @return List of notifications
     */
    List<NotificationResponse> getNotificationsByRecipient(String recipientId);

    /**
     * Get all unread in-app notifications for a recipient.
     *
     * @param recipientId The user's UUID
     * @return List of unread notifications
     */
    List<NotificationResponse> getUnreadNotifications(String recipientId);

    /**
     * Mark a notification as read.
     *
     * @param id The notification ID
     * @return The updated notification
     */
    NotificationResponse markAsRead(Long id);

    /**
     * Mark all notifications as read for a recipient.
     *
     * @param recipientId The user's UUID
     */
    void markAllAsRead(String recipientId);

    /**
     * Delete a notification by ID.
     *
     * @param id The notification ID
     */
    void deleteNotification(Long id);

    /**
     * Get count of unread notifications for a recipient.
     *
     * @param recipientId The user's UUID
     * @return Count of unread notifications
     */
    Long getUnreadCount(String recipientId);

    /**
     * Process pending notifications and attempt delivery.
     * Called by scheduled job.
     *
     * @return Number of notifications processed
     */
    int processPendingNotifications();

    /**
     * Retry failed notifications.
     * Called by scheduled job.
     *
     * @return Number of notifications retried
     */
    int retryFailedNotifications();

    /**
     * Get all notifications by status.
     *
     * @param status The notification status
     * @return List of notifications
     */
    List<NotificationResponse> getNotificationsByStatus(NotificationStatus status);
}