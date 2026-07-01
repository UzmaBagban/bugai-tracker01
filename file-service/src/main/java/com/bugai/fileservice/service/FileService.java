package com.bugai.fileservice.service;

import com.bugai.fileservice.dto.FileRecordDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * FileService Interface defines the contract for file storage operations.
 * Implementations handle:
 *   - File upload to local filesystem
 *   - File download from filesystem
 *   - File deletion (soft delete)
 *   - Metadata retrieval
 *
 * This interface provides abstraction, making it easy to swap implementations
 * (e.g., from local filesystem to cloud storage like S3).
 */
public interface FileService {

    /**
     * Upload a file to the local filesystem and create a database record.
     *
     * File is stored at: /uploads/YYYY-MM-DD/fileId.ext
     * Database record tracks metadata (name, size, type, path, owner).
     *
     * Validation:
     *   - File size must not exceed FileType's maximum limit
     *   - File cannot be null or empty
     *   - uploadedBy cannot be null (user ID required)
     *
     * @param file MultipartFile containing the file data and metadata
     * @param uploadedBy User ID (UUID) of the uploader (from JWT in Phase 2)
     * @return FileRecordDTO containing uploaded file metadata and fileId
     * throws FileSizeExceededException if file exceeds type's size limit
     * throws FileStorageException if disk write operation fails
     */
    FileRecordDTO uploadFile(MultipartFile file, String uploadedBy);

    /**
     * Retrieve metadata for an active file by its ID.
     * Does not return actual file content; only metadata.
     *
     * @param fileId Unique file identifier (UUID)
     * @return FileRecordDTO containing file metadata
     * throws FileNotFoundException if file not found or is deleted
     */
    FileRecordDTO getFileMetadata(String fileId);

    /**
     * Get the local filesystem path for a file.
     * Used internally by controller to serve file content.
     *
     * @param fileId Unique file identifier (UUID)
     * @return Absolute filesystem path to the file
     * throws FileNotFoundException if file not found or is deleted
     */
    String getFilePath(String fileId);

    /**
     * Soft-delete a file (mark as inactive).
     * File is NOT permanently removed from disk; instead, active flag is set to false.
     * This allows recovery if needed and maintains audit trail.
     *
     * @param fileId Unique file identifier (UUID)
     * throws FileNotFoundException if file not found or already deleted
     */
    void deleteFile(String fileId);

    /**
     * List all active files uploaded by a specific user.
     * Useful for user file management or quota displays.
     *
     * @param uploadedBy User ID (UUID)
     * @return List of FileRecordDTOs for all active files by this user
     */
    List<FileRecordDTO> getFilesByUser(String uploadedBy);

    /**
     * Verify that a file exists and is active.
     * Lightweight check used before download/deletion.
     *
     * @param fileId Unique file identifier (UUID)
     * @return true if file exists and is active, false otherwise
     */
    boolean fileExists(String fileId);
}