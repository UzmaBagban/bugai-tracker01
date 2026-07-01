package com.bugai.notificationservice.dto;


import com.bugai.notificationservice.enums.NotificationChannel;
import com.bugai.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    // UUID of the user receiving the notification
    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    // UUID of the bug (optional, can be null for non-bug notifications)
    private UUID bugId;

    // Type of notification event
    @NotNull(message = "Notification type cannot be null")
    private NotificationType type;

    // Channel through which to send the notification
    @NotNull(message = "Notification channel cannot be null")
    private NotificationChannel channel;

    // Subject line (mainly for EMAIL)
    @NotBlank(message = "Subject cannot be blank")
    private String subject;

    // Detailed message content
    @NotBlank(message = "Message cannot be blank")
    private String message;

    // Recipient contact info (email, phone, slack handle)
    @NotBlank(message = "Recipient cannot be blank")
    private String recipient;
}