package com.bugai.fileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bugai.fileservice.enums.FileType;
import java.time.LocalDateTime;

/**
 * FileRecordDTO is the Data Transfer Object for FileRecord entity.
 * Used for:
 *   1. Serializing file metadata in API responses
 *   2. Accepting file metadata in requests
 *   3. Hiding sensitive internal details (e.g., actual file path)
 *
 * Note: The actual file content is transferred via multipart/form-data;
 * this DTO handles metadata only.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRecordDTO {

    /**
     * Unique file identifier returned to client.
     * Client uses this fileId for all subsequent file operations
     * (download, delete, metadata retrieval).
     */
    private String fileId;

    /**
     * Original name of the uploaded file.
     * Displayed to users; used for download "Save As" dialog.
     */
    private String fileName;

    /**
     * File size in bytes.
     * Helps clients estimate download time or warn about large files.
     */
    private long fileSize;

    /**
     * MIME type of the file (e.g., "application/pdf").
     * Used by browsers to determine how to handle the file.
     */
    private String mimeType;

    /**
     * File type category (DOCUMENT, IMAGE, ATTACHMENT, etc.).
     * Useful for UI filtering or validation logic.
     */
    private FileType fileType;

    /**
     * User ID (UUID) who uploaded the file.
     * For audit and access control purposes.
     */
    private String uploadedBy;

    /**
     * Timestamp when the file was uploaded.
     * Useful for sorting and filtering by date.
     */
    private LocalDateTime uploadedAt;

    /**
     * Current status of the file (active or deleted).
     * true = file is available for download
     * false = file has been soft-deleted and is not accessible
     */
    private Boolean active;
}