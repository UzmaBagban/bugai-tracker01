package com.bugai.analyticsservice.exception;

/**
 * Exception thrown when an analytics record is not found.
 */
public class AnalyticsNotFoundException extends RuntimeException {
    public AnalyticsNotFoundException(String message) {
        super(message);
    }

    public AnalyticsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
