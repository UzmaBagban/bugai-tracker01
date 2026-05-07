package com.bugai.bugservice.exception;


/**
 * ResourceNotFoundException - thrown when a requested resource doesn't exist
 * Extends RuntimeException so it's unchecked
 * Will be caught by GlobalExceptionHandler and returned as 404
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with message
     *
     * @param message Error message describing which resource wasn't found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message Error message
     * @param cause Original exception that caused this
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}