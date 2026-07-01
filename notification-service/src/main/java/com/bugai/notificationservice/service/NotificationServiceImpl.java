package com.bugai.notificationservice.service;


import com.bugai.notificationservice.dto.NotificationRequest;
import com.bugai.notificationservice.dto.NotificationResponse;
import com.bugai.notificationservice.entity.Notification;
import com.bugai.notificationservice.enums.NotificationStatus;
import com.bugai.notificationservice.exception.NotificationNotFoundException;
import com.bugai.notificationservice.repository.NotificationRepository;
import com.bugai.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        // Build the notification entity from the request DTO
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(request.getUserId())
                .bugId(request.getBugId())
                .type(request.getType())
                .channel(request.getChannel())
                .status(NotificationStatus.PENDING) // New notifications start as PENDING
                .subject(request.getSubject())
                .message(request.getMessage())
                .recipient(request.getRecipient())
                .retryCount(0) // Initialize retry count to 0
                .build();

        // Save the notification to the database
        Notification savedNotification = notificationRepository.save(notification);

        // Convert to response DTO and return
        return toResponseDTO(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(UUID id) {
        // Fetch notification by ID, throw exception if not found
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        // Convert to response DTO and return
        return toResponseDTO(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(UUID userId) {
        // Fetch all notifications for the user and convert to response DTOs
        return notificationRepository.findByUserId(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(UUID userId) {
        // Fetch unread notifications (status = DELIVERED) for the user
        return notificationRepository.findUnreadNotifications(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByBugId(UUID bugId) {
        // Fetch all notifications related to a specific bug
        return notificationRepository.findByBugId(bugId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getPendingNotifications() {
        // Fetch all notifications with PENDING status (not yet sent)
        return notificationRepository.findByStatus(NotificationStatus.PENDING).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse updateNotificationStatus(UUID id, NotificationStatus status) {
        // Fetch the notification by ID, throw exception if not found
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        // Update the status
        notification.setStatus(status);

        // If status is SENT, record the timestamp
        if (status == NotificationStatus.SENT) {
            notification.setSentAt(LocalDateTime.now());
        }

        // Save and convert to response DTO
        Notification updatedNotification = notificationRepository.save(notification);
        return toResponseDTO(updatedNotification);
    }

    @Override
    public List<NotificationResponse> retryFailedNotifications() {
        // Fetch all failed notifications eligible for retry (retryCount < 3)
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsForRetry();

        // Increment retry count and reset status to PENDING for re-sending
        failedNotifications.forEach(notification -> {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setStatus(NotificationStatus.PENDING);
            notification.setErrorMessage(null); // Clear previous error message
        });

        // Save all updated notifications
        List<Notification> savedNotifications = notificationRepository.saveAll(failedNotifications);

        // Convert to response DTOs and return
        return savedNotifications.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNotification(UUID id) {
        // Check if notification exists, throw exception if not
        if (!notificationRepository.existsById(id)) {
            throw new NotificationNotFoundException("Notification not found with id: " + id);
        }

        // Delete the notification from database
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsInTimeRange(UUID userId, LocalDateTime startTime, LocalDateTime endTime) {
        // Fetch notifications created within the specified time range
        return notificationRepository.findNotificationsInTimeRange(userId, startTime, endTime).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPendingNotificationCount(UUID userId) {
        // Return count of pending notifications for the user (useful for dashboard badge)
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.PENDING);
    }

    // Private helper method to convert Notification entity to NotificationResponse
    private NotificationResponse toResponseDTO(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .bugId(notification.getBugId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .status(notification.getStatus())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .recipient(notification.getRecipient())
                .errorMessage(notification.getErrorMessage())
                .retryCount(notification.getRetryCount())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
}
