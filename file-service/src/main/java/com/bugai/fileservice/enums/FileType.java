package com.bugai.fileservice.enums;

/**
 * FileType Enum defines supported file types and their respective size limits.
 * Each file type has a maximum allowed file size in bytes.
 *
 * Example:
 *   - DOCUMENT: 10 MB (10,485,760 bytes)
 *   - IMAGE: 5 MB (5,242,880 bytes)
 *   - ATTACHMENT: 20 MB (20,971,520 bytes)
 */
public enum FileType {
    // Document files (PDFs, Word docs, etc.) - 10 MB max
    DOCUMENT(10 * 1024 * 1024),

    // Image files (PNG, JPG, GIF) - 5 MB max
    IMAGE(5 * 1024 * 1024),

    // Generic attachments (logs, config files, etc.) - 20 MB max
    ATTACHMENT(20 * 1024 * 1024),

    // Compressed archives (ZIP, RAR) - 50 MB max
    ARCHIVE(50 * 1024 * 1024),

    // Video files - 100 MB max
    VIDEO(100 * 1024 * 1024),

    // Other/Unknown files - 15 MB max as fallback
    OTHER(15 * 1024 * 1024);

    // Maximum file size in bytes for this file type
    private final long maxSizeBytes;

    /**
     * Constructor to initialize FileType with maximum size constraint.
     *
     * @param maxSizeBytes Maximum allowed file size in bytes
     */
    FileType(long maxSizeBytes) {
        this.maxSizeBytes = maxSizeBytes;
    }

    /**
     * Get the maximum allowed file size for this file type.
     *
     * @return Maximum size in bytes
     */
    public long getMaxSizeBytes() {
        return maxSizeBytes;
    }

    /**
     * Determine FileType based on file extension.
     * Provides intelligent mapping from common file extensions to FileType.
     *
     * @param fileName Name of the file (with or without path)
     * @return Appropriate FileType, or OTHER if not recognized
     */
    public static FileType fromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return OTHER;
        }

        // Extract file extension (everything after the last dot)
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1)
                .toLowerCase();

        // Match extension to FileType
        switch (extension) {
            // Document types
            case "pdf":
            case "doc":
            case "docx":
            case "txt":
            case "xlsx":
            case "xls":
            case "ppt":
            case "pptx":
                return DOCUMENT;

            // Image types
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
            case "svg":
                return IMAGE;

            // Archive types
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
                return ARCHIVE;

            // Video types
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
            case "flv":
            case "wmv":
                return VIDEO;

            // Log and config files
            case "log":
            case "cfg":
            case "conf":
            case "properties":
            case "xml":
            case "json":
            case "yaml":
                return ATTACHMENT;

            // Default to OTHER for unrecognized types
            default:
                return OTHER;
        }
    }
}