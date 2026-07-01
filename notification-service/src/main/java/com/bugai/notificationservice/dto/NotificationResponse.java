package com.bugai.notificationservice.dto;

import com.bugai.notificationservice.enums.NotificationChannel;
import com.bugai.notificationservice.enums.NotificationStatus;
import com.bugai.notificationservice.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private UUID bugId;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String subject;
    private String message;
    private String recipient;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
}