package com.bugai.bugservice.dto;


import lombok.*;

import java.time.LocalDateTime;

/**
 * ErrorResponse DTO - standardized error response format
 * Returned by GlobalExceptionHandler for all exceptions
 * Provides consistent structure for clients to parse errors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Timestamp when error occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code (404, 400, 500, etc.)
     */
    private int status;

    /**
     * HTTP status reason phrase (Not Found, Bad Request, etc.)
     */
    private String error;

    /**
     * Detailed error message
     */
    private String message;

    /**
     * Request path that caused the error
     */
    private String path;
}