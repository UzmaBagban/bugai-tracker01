package com.bugai.notificationservice.repository;

import com.bugai.notificationservice.entity.Notification;
import com.bugai.notificationservice.enums.NotificationChannel;
import com.bugai.notificationservice.enums.NotificationStatus;
import com.bugai.notificationservice.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Find all notifications for a specific user
    List<Notification> findByUserId(UUID userId);

    // Find all notifications for a user with a specific status
    List<Notification> findByUserIdAndStatus(UUID userId, NotificationStatus status);

    // Find all notifications for a user by channel
    List<Notification> findByUserIdAndChannel(UUID userId, NotificationChannel channel);

    // Find all notifications related to a specific bug
    List<Notification> findByBugId(UUID bugId);

    // Find all pending notifications (not yet sent)
    List<Notification> findByStatus(NotificationStatus status);

    // Find all failed notifications that can be retried
    @Query("SELECT n FROM Notification n WHERE n.status = com.bugai.notificationservice.enums.NotificationStatus.FAILED AND n.retryCount < 3")
    List<Notification> findFailedNotificationsForRetry();

    // Find notifications created within a specific time range
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt BETWEEN :startTime AND :endTime")
    List<Notification> findNotificationsInTimeRange(
            @Param("userId") UUID userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // Find all unread notifications for a user (status = DELIVERED, not yet READ)
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.status = com.bugai.notificationservice.enums.NotificationStatus.DELIVERED")
    List<Notification> findUnreadNotifications(@Param("userId") UUID userId);

    // Count total notifications for a user
    Long countByUserId(UUID userId);

    // Count pending notifications for a user
    Long countByUserIdAndStatus(UUID userId, NotificationStatus status);
}