// InvalidNotificationRequestException.java
package com.bugai.notificationservice.exception;

public class InvalidNotificationRequestException extends RuntimeException {
    public InvalidNotificationRequestException(String message) {
        super(message);
    }

    public InvalidNotificationRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}