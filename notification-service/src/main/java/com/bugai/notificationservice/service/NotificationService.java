package com.bugai.notificationservice.service;

import com.bugai.notificationservice.dto.NotificationRequest;
import com.bugai.notificationservice.dto.NotificationResponse;
import com.bugai.notificationservice.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    // Create a new notification
    NotificationResponse createNotification(NotificationRequest request);

    // Retrieve a notification by ID
    NotificationResponse getNotificationById(UUID id);

    // Retrieve all notifications for a specific user
    List<NotificationResponse> getNotificationsByUserId(UUID userId);

    // Retrieve unread notifications for a user
    List<NotificationResponse> getUnreadNotifications(UUID userId);

    // Retrieve notifications for a bug
    List<NotificationResponse> getNotificationsByBugId(UUID bugId);

    // Retrieve pending notifications (not yet sent)
    List<NotificationResponse> getPendingNotifications();

    // Update notification status (e.g., mark as READ, SENT, FAILED)
    NotificationResponse updateNotificationStatus(UUID id, NotificationStatus status);

    // Retry sending failed notifications
    List<NotificationResponse> retryFailedNotifications();

    // Delete a notification (hard delete)
    void deleteNotification(UUID id);

    // Get notifications created within a time range for a user
    List<NotificationResponse> getNotificationsInTimeRange(UUID userId, LocalDateTime startTime, LocalDateTime endTime);

    // Get count of pending notifications for a user (useful for dashboard)
    Long getPendingNotificationCount(UUID userId);
}