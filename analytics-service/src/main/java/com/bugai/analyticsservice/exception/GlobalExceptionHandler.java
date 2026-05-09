package com.bugai.analyticsservice.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Analytics Service.
 *
 * Catches exceptions thrown by controllers and service layer,
 * then converts them into standardized HTTP responses.
 *
 * @RestControllerAdvice applies this handler to all @RestController classes
 * in the application, ensuring consistent error responses across all endpoints.
 *
 * Benefits:
 * - Centralized error handling (no try-catch in controllers)
 * - Consistent error response format for clients
 * - Proper HTTP status codes for different error types
 * - Detailed logging for debugging
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException.
     *
     * Thrown when:
     * - Analytics record not found by ID
     * - Analytics not found for specific date/project/team/developer
     *
     * Returns HTTP 404 Not Found with error details.
     *
     * @param ex The caught exception
     * @return ResponseEntity with error details and HTTP 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());

        // Build standardized error response
        return build(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage()
        );
    }

    /**
     * Handle MethodArgumentNotValidException.
     *
     * Thrown automatically by Spring when @Valid validation fails on request DTOs.
     * For example: missing required fields, invalid date formats, etc.
     *
     * Returns HTTP 400 Bad Request with detailed field-level errors.
     *
     * @param ex The caught validation exception
     * @return ResponseEntity with validation errors and HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());

        // Extract field-specific errors from exception
        // Each field that failed validation gets its own error message
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        // Build response with all validation errors
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle IllegalArgumentException.
     *
     * Thrown when service layer detects invalid business logic conditions.
     * For example: end date before start date, invalid date range, etc.
     *
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The caught exception
     * @return ResponseEntity with error details and HTTP 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());

        return build(
                HttpStatus.BAD_REQUEST,
                "Invalid Request",
                ex.getMessage()
        );
    }

    /**
     * Handle all other unexpected exceptions.
     *
     * This is a catch-all for any exceptions not handled by specific handlers above.
     * Ensures clients always get a proper error response, never raw stack traces.
     *
     * Returns HTTP 500 Internal Server Error.
     *
     * @param ex The caught exception
     * @return ResponseEntity with generic error message and HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later."
        );
    }

    /**
     * Private helper method to build standardized error response.
     *
     * Creates a consistent JSON structure for all error responses:
     * {
     *   "timestamp": "2024-01-15T10:30:00",
     *   "status": 404,
     *   "error": "Resource Not Found",
     *   "message": "Analytics not found for date: 2024-01-15"
     * }
     *
     * This pattern is used across all services in BugAI Tracker for consistency.
     *
     * Note: This is a different "build" concept than Lombok's builder() pattern.
     * This is a custom helper method for error response construction.
     *
     * @param status HTTP status code to return
     * @param error Short error type description
     * @param message Detailed error message
     * @return ResponseEntity with error details and specified status
     */
    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);

        return ResponseEntity.status(status).body(errorResponse);
    }
}
