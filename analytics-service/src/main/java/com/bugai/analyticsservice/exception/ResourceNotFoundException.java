package com.bugai.analyticsservice.exception;


/**
 * Custom exception thrown when a requested resource is not found.
 *
 * Used specifically for:
 * - Analytics record not found by ID
 * - Analytics not found for a specific date
 * - Analytics not found for date/project/team/developer combination
 *
 * This is a RuntimeException, so it doesn't need to be declared in method signatures.
 * Spring's exception handler will catch this and return appropriate HTTP 404 responses.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construct exception with a custom message.
     *
     * @param message Description of what resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Construct exception with a custom message and root cause.
     *
     * @param message Description of what resource was not found
     * @param cause The underlying exception that caused this
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}