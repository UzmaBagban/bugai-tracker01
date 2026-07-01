package com.bugai.fileservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bugai.fileservice.enums.FileType;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * FileRecord Entity represents a stored file in the File Service.
 * Each file record maintains metadata about uploaded files including:
 *   - Unique file ID (UUID)
 *   - File metadata (name, size, type, MIME type)
 *   - Storage information (local file path based on upload date)
 *   - Ownership and audit trail (uploadedBy, timestamps)
 *   - Soft delete flag for logical deletion
 *
 * Files are stored on the local filesystem using date-based directory structure:
 *   Format: /uploads/YYYY-MM-DD/fileId.ext
 *   Example: /uploads/2024-12-20/a1b2c3d4-e5f6-4g7h-8i9j-0k1l2m3n4o5p.pdf
 */
@Entity
@Table(name = "files", schema = "bug_ai_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRecord {

    // ===== PRIMARY KEY & IDENTIFIERS =====

    /**
     * Unique file identifier (UUID).
     * Auto-generated on file upload; never changes.
     * Used in file retrieval and deletion operations.
     */
    @Id
    @Column(name = "file_id", columnDefinition = "CHAR(36)")
    private String fileId;

    // ===== FILE METADATA =====

    /**
     * Original name of the uploaded file (with extension).
     * Example: "bug-report.pdf"
     * Stored for user reference; does not affect storage.
     */
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /**
     * Size of the file in bytes.
     * Used for validation against FileType size limits
     * and for quota management in future versions.
     */
    @Column(name = "file_size", nullable = false)
    private long fileSize;

    /**
     * MIME type of the file (e.g., "application/pdf", "image/png").
     * Helps clients know how to display/handle the file.
     */
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    /**
     * Enumerated file type (DOCUMENT, IMAGE, ATTACHMENT, etc.).
     * Determines maximum allowed file size.
     * Mapped to database via @Enumerated(STRING).
     */
    @Column(name = "file_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    /**
     * Local filesystem path where the file is stored.
     * Format: /uploads/YYYY-MM-DD/fileId.ext
     * Example: /uploads/2024-12-20/550e8400-e29b-41d4-a716-446655440000.pdf
     *
     * Relative to application's working directory or configured base path.
     */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    // ===== OWNERSHIP & AUDIT TRAIL =====

    /**
     * User ID (UUID) of the user who uploaded this file.
     * In Phase 2, this will be extracted from JWT token via API Gateway.
     * Currently accepted as a request parameter.
     * Used for access control and audit logging.
     */
    @Column(name = "uploaded_by", nullable = false, columnDefinition = "CHAR(36)")
    private String uploadedBy;

    /**
     * Timestamp when the file was uploaded (UTC).
     * Set automatically via @CreationTimestamp.
     * Never modified after creation.
     */
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    /**
     * Timestamp when the file record was last modified (UTC).
     * Updated on any change (e.g., when soft-deleted).
     * Useful for audit trail and troubleshooting.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ===== SOFT DELETE =====

    /**
     * Soft delete flag. Files are never permanently deleted from disk;
     * instead, active is set to false. This allows for recovery and
     * maintains historical records.
     *
     * Default: true (active)
     * When deleted: active = false
     */
    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean active = true;

    // ===== LIFECYCLE CALLBACKS =====

    /**
     * Called automatically before a new FileRecord is persisted.
     * Initializes timestamps and generates UUID if not already set.
     */
    @PrePersist
    protected void onCreate() {
        // Generate UUID if not already set
        if (this.fileId == null) {
            this.fileId = UUID.randomUUID().toString();
        }
        // Initialize timestamps to current UTC time
        LocalDateTime now = LocalDateTime.now();
        this.uploadedAt = now;
        this.updatedAt = now;
        // Set active to true if not explicitly set
        if (this.active == null) {
            this.active = true;
        }
    }

    /**
     * Called automatically before an existing FileRecord is updated.
     * Updates the modified timestamp to reflect the change.
     */
    @PreUpdate
    protected void onUpdate() {
        // Always update the modified timestamp
        this.updatedAt = LocalDateTime.now();
    }
}