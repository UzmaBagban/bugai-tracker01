package com.bugai.notificationservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handling for the Notification Service.
 * Catches specific and generic exceptions and returns structured JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── Handlers ────────────────────────────────────────────────────────────

    /**
     * Handles cases where a notification record is not found.
     * Returns HTTP 404.
     */
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotificationNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles @Valid / @Validated failures on request DTOs.
     * Returns HTTP 400 with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // Collect all field errors into a single readable message
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);

        return build(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * Catch-all for any unexpected runtime exceptions.
     * Returns HTTP 500 to avoid leaking internals.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    // ─── Private Helper ───────────────────────────────────────────────────────

    /**
     * Builds a consistent error response body shared by all handlers.
     *
     * @param status  HTTP status to return
     * @param message Human-readable error description
     * @return structured ResponseEntity with timestamp, status, and message
     */
    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
