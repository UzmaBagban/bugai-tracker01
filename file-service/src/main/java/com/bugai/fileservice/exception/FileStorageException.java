package com.bugai.fileservice.exception;

/**
 * FileStorageException is thrown when file storage operations fail.
 * Examples:
 *   - Failed to write file to disk
 *   - Failed to delete file from disk
 *   - Disk I/O errors
 *   - Insufficient disk space
 */
public class FileStorageException extends RuntimeException {

    /**
     * Constructor with error message.
     *
     * @param message Descriptive error message
     */
    public FileStorageException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and root cause.
     *
     * @param message Descriptive error message
     * @param cause The underlying exception (e.g., IOException)
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}