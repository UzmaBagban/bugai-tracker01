package com.bugai.fileservice.exception;

/**
 * FileNotFoundException is thrown when a requested file cannot be found
 * in the database or on the filesystem.
 *
 * Results in HTTP 404 Not Found response.
 */
public class FileNotFoundException extends RuntimeException {

    /**
     * Constructor with file ID that was not found.
     * Constructs message: "File not found with ID: {fileId}"
     *
     * @param fileId The UUID of the file that was not found
     */
    public FileNotFoundException(String fileId) {
        super("File not found with ID: " + fileId);
    }

    /**
     * Constructor with custom error message and root cause.
     * Useful when you need to preserve the original exception stack trace.
     *
     * @param message Custom error message
     * @param cause The underlying exception that caused this error
     */
    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with only a custom error message.
     * Use this when you don't have a root cause exception.
     *
     * Note: This is called by RuntimeException(String) internally,
     * so we rely on the parent class implementation.
     */
    // No need to override if just calling super(message) — use the first constructor instead
}