package com.bugai.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler provides centralized exception handling for the File Service.
 * Catches exceptions and returns consistent, informative error responses.
 *
 * Benefits:
 *   - Consistent error response format across all endpoints
 *   - Appropriate HTTP status codes (400, 404, 500, etc.)
 *   - Detailed error messages for debugging
 *   - Stack trace information for server-side logging
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle FileNotFoundException (HTTP 404).
     * When a requested file cannot be found.
     *
     * @param ex The FileNotFoundException
     * @return ResponseEntity with 404 status and error details
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFileNotFound(FileNotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "File Not Found",
                ex.getMessage()
        );
    }

    /**
     * Handle FileSizeExceededException (HTTP 400).
     * When an uploaded file exceeds the size limit for its type.
     *
     * @param ex The FileSizeExceededException
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleFileSizeExceeded(
            FileSizeExceededException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "File Size Exceeded",
                ex.getMessage()
        );
    }

    /**
     * Handle FileStorageException (HTTP 500).
     * When file storage operations fail (disk errors, I/O issues).
     *
     * @param ex The FileStorageException
     * @return ResponseEntity with 500 status and error details
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, Object>> handleFileStorageException(
            FileStorageException ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "File Storage Error",
                ex.getMessage()
        );
    }

    /**
     * Handle all other unexpected exceptions (HTTP 500).
     * Catch-all for unhandled exceptions.
     *
     * @param ex The unexpected exception
     * @return ResponseEntity with 500 status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage()
        );
    }

    /**
     * Helper method to build consistent error response format.
     * Returns a structured error response with timestamp, status, message, etc.
     *
     * @param status HTTP status code
     * @param errorTitle Brief error title
     * @param errorMessage Detailed error message
     * @return ResponseEntity with formatted error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String errorTitle, String errorMessage) {

        // Build error response map
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", errorTitle);
        errorResponse.put("message", errorMessage);

        return new ResponseEntity<>(errorResponse, status);
    }
}