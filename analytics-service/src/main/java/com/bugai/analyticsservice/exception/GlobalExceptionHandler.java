package com.bugai.analyticsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for Analytics Service.
 * Centralized error response formatting across all endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle AnalyticsNotFoundException (404).
     * Triggered when querying for non-existent analytics records.
     *
     * @param ex The exception thrown
     * @return Error response with 404 status
     */
    @ExceptionHandler(AnalyticsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAnalyticsNotFoundException(AnalyticsNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(build(
                HttpStatus.NOT_FOUND.value(),
                "Analytics Not Found",
                ex.getMessage()
        ));
    }

    /**
     * Handle validation exceptions (400).
     * Triggered when request DTOs fail @Valid annotation validation.
     *
     * @param ex The exception thrown
     * @return Error response with 400 status and field-level error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract field-level validation errors
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = build(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Request validation failed"
        );
        response.put("fieldErrors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle all other exceptions (500).
     * Catch-all for unexpected runtime exceptions.
     *
     * @param ex The exception thrown
     * @return Error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(build(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
        ));
    }

    /**
     * Private helper method to build consistent error response structure.
     * Reduces code duplication in exception handlers.
     *
     * @param status HTTP status code
     * @param error Error type/title
     * @param message Detailed error message
     * @return Map containing error details
     */
    private Map<String, Object> build(int status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}