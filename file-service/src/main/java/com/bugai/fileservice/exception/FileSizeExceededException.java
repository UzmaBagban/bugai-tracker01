package com.bugai.fileservice.exception;

/**
 * FileSizeExceededException is thrown when an uploaded file exceeds
 * the maximum size limit for its file type.
 *
 * Results in HTTP 400 Bad Request response.
 */
public class FileSizeExceededException extends RuntimeException {

    /**
     * Constructor with details about size violation.
     *
     * @param fileName Name of the file that exceeded size limit
     * @param fileSize Actual size of the file in bytes
     * @param maxSize Maximum allowed size in bytes for this file type
     */
    public FileSizeExceededException(String fileName, long fileSize, long maxSize) {
        super(String.format(
                "File '%s' exceeds maximum size limit. " +
                        "File size: %d bytes, Max allowed: %d bytes",
                fileName, fileSize, maxSize
        ));
    }
}