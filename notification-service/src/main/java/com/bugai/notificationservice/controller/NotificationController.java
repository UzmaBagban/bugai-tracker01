package com.bugai.notificationservice.controller;


import com.bugai.notificationservice.dto.NotificationRequest;
import com.bugai.notificationservice.dto.NotificationResponse;
import com.bugai.notificationservice.entity.Notification;
import com.bugai.notificationservice.enums.NotificationStatus;
import com.bugai.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // POST: Create a new notification
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Retrieve a notification by ID
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable UUID id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    // GET: Retrieve all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable UUID userId) {
        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    // GET: Retrieve unread notifications for a user
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@PathVariable UUID userId) {
        List<NotificationResponse> responses = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(responses);
    }

    // GET: Retrieve notifications related to a bug
    @GetMapping("/bug/{bugId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByBugId(@PathVariable UUID bugId) {
        List<NotificationResponse> responses = notificationService.getNotificationsByBugId(bugId);
        return ResponseEntity.ok(responses);
    }

    // GET: Retrieve all pending notifications
    @GetMapping("/pending")
    public ResponseEntity<List<NotificationResponse>> getPendingNotifications() {
        List<NotificationResponse> responses = notificationService.getPendingNotifications();
        return ResponseEntity.ok(responses);
    }

    // PUT: Update notification status
    @PutMapping("/{id}/status")
    public ResponseEntity<NotificationResponse> updateNotificationStatus(
            @PathVariable UUID id,
            @RequestParam NotificationStatus status) {
        NotificationResponse response = notificationService.updateNotificationStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // POST: Retry sending failed notifications
    @PostMapping("/retry-failed")
    public ResponseEntity<List<NotificationResponse>> retryFailedNotifications() {
        List<NotificationResponse> responses = notificationService.retryFailedNotifications();
        return ResponseEntity.ok(responses);
    }

    // DELETE: Delete a notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // GET: Retrieve notifications within a time range
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<NotificationResponse>> getNotificationsInTimeRange(
            @PathVariable UUID userId,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        List<NotificationResponse> responses = notificationService.getNotificationsInTimeRange(userId, startTime, endTime);
        return ResponseEntity.ok(responses);
    }

    // GET: Get pending notification count for a user
    @GetMapping("/user/{userId}/pending-count")
    public ResponseEntity<Long> getPendingNotificationCount(@PathVariable UUID userId) {
        Long count = notificationService.getPendingNotificationCount(userId);
        return ResponseEntity.ok(count);
    }
}