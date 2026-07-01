package com.bugai.fileservice.service;

import com.bugai.fileservice.dto.FileRecordDTO;
import com.bugai.fileservice.entity.FileRecord;
import com.bugai.fileservice.enums.FileType;
import com.bugai.fileservice.exception.FileNotFoundException;
import com.bugai.fileservice.exception.FileSizeExceededException;
import com.bugai.fileservice.exception.FileStorageException;
import com.bugai.fileservice.repository.FileRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * FileServiceImpl provides the core file storage logic for the File Service.
 *
 * Key Features:
 *   1. Local Filesystem Storage: Files stored in /uploads/YYYY-MM-DD/ directories
 *   2. Database Metadata: FileRecord entities track file metadata and ownership
 *   3. Size Validation: Enforces FileType-specific size limits
 *   4. Soft Deletion: Files marked as inactive instead of permanent deletion
 *   5. Audit Trail: uploadedAt, updatedAt timestamps maintained automatically
 *
 * Configuration:
 *   - Base upload directory: Configurable via file.upload.dir property
 *   - Defaults to: ./uploads (relative to application working directory)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    // Inject the repository for database operations
    private final FileRecordRepository fileRecordRepository;

    // Base directory for file storage (configurable via application.properties)
    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    /**
     * Upload a file to the local filesystem and create a database record.
     *
     * Process:
     *   1. Determine FileType from file extension
     *   2. Validate file size against FileType limit
     *   3. Generate UUID for the file
     *   4. Create date-based directory structure (YYYY-MM-DD)
     *   5. Write file to disk
     *   6. Create FileRecord entity with metadata
     *   7. Persist to database
     *   8. Return FileRecordDTO
     *
     * @param file MultipartFile containing file data
     * @param uploadedBy User ID uploading the file
     * @return FileRecordDTO with file metadata and assigned fileId
     */
    @Override
    public FileRecordDTO uploadFile(MultipartFile file, String uploadedBy) {
        // Validate input parameters
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File cannot be null or empty");
        }
        if (uploadedBy == null || uploadedBy.isEmpty()) {
            throw new FileStorageException("Uploader ID (uploadedBy) cannot be null");
        }

        // Determine file type based on extension
        String originalFileName = file.getOriginalFilename();
        FileType fileType = FileType.fromFileName(originalFileName);

        // Validate file size against FileType limit
        long fileSize = file.getSize();
        long maxAllowedSize = fileType.getMaxSizeBytes();
        if (fileSize > maxAllowedSize) {
            throw new FileSizeExceededException(originalFileName, fileSize, maxAllowedSize);
        }

        // Generate unique file ID and create storage directory
        String fileId = UUID.randomUUID().toString();
        Path uploadPath = createUploadDirectory();

        // Extract file extension from original filename
        String fileExtension = extractFileExtension(originalFileName);
        // Construct the new filename: fileId.ext (e.g., a1b2c3d4.pdf)
        String storedFileName = fileId + "." + fileExtension;

        // Full filesystem path to store the file
        Path filePath = uploadPath.resolve(storedFileName);

        try {
            // Write file to disk
            Files.write(filePath, file.getBytes());

            // Construct relative file path for database storage
            // Format: /uploads/YYYY-MM-DD/fileId.ext
            String relativePath = "/uploads/" +
                    LocalDate.now() + "/" +
                    storedFileName;

            // Create FileRecord entity to persist metadata
            FileRecord fileRecord = FileRecord.builder()
                    .fileId(fileId)
                    .fileName(originalFileName)
                    .fileSize(fileSize)
                    .mimeType(file.getContentType())
                    .fileType(fileType)
                    .filePath(relativePath)
                    .uploadedBy(uploadedBy)
                    .active(true)
                    .build();

            // Save to database
            FileRecord savedRecord = fileRecordRepository.save(fileRecord);

            // Return DTO to client
            return mapToDTO(savedRecord);

        } catch (IOException e) {
            // If write fails, throw FileStorageException
            throw new FileStorageException(
                    "Failed to write file to disk: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve file metadata by file ID.
     * Only returns metadata, not the actual file content.
     *
     * @param fileId The file's UUID
     * @return FileRecordDTO containing file metadata
     * @throws FileNotFoundException if file not found or is deleted
     */
    @Override
    @Transactional(readOnly = true)
    public FileRecordDTO getFileMetadata(String fileId) {
        // Query database for active file with this ID
        FileRecord fileRecord = fileRecordRepository.findByFileIdAndActive(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // Convert entity to DTO
        return mapToDTO(fileRecord);
    }

    /**
     * Get the filesystem path for a file (used for serving downloads).
     *
     * @param fileId The file's UUID
     * @return Absolute filesystem path
     * @throws FileNotFoundException if file not found
     */
    @Override
    @Transactional(readOnly = true)
    public String getFilePath(String fileId) {
        // Retrieve file record from database
        FileRecord fileRecord = fileRecordRepository.findByFileIdAndActive(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // Return the stored file path
        return fileRecord.getFilePath();
    }

    /**
     * Soft-delete a file (mark as inactive).
     * File remains on disk but is hidden from queries and downloads.
     *
     * @param fileId The file's UUID
     * @throws FileNotFoundException if file not found or already deleted
     */
    @Override
    public void deleteFile(String fileId) {
        // Retrieve active file record
        FileRecord fileRecord = fileRecordRepository.findByFileIdAndActive(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // Mark as inactive (soft delete)
        fileRecord.setActive(false);
        // updatedAt is automatically set via @PreUpdate

        // Save the change to database
        fileRecordRepository.save(fileRecord);
    }

    /**
     * List all active files uploaded by a specific user.
     *
     * @param uploadedBy User ID (UUID)
     * @return List of FileRecordDTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<FileRecordDTO> getFilesByUser(String uploadedBy) {
        // Query all active files by this user
        List<FileRecord> fileRecords = fileRecordRepository.findByUploadedByAndActive(uploadedBy);

        // Convert each entity to DTO
        return fileRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if a file exists and is active.
     *
     * @param fileId The file's UUID
     * @return true if file exists and is active, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean fileExists(String fileId) {
        return fileRecordRepository.existsByFileIdAndActive(fileId);
    }

    // ===== PRIVATE HELPER METHODS =====

    /**
     * Create date-based upload directory if it doesn't exist.
     *
     * Directory structure: /uploads/YYYY-MM-DD/
     * This keeps files organized by upload date and limits files per directory.
     *
     * @return Path object pointing to the upload directory
     * @throws FileStorageException if directory creation fails
     */
    private Path createUploadDirectory() {
        try {
            // Get current date for directory structure
            LocalDate today = LocalDate.now();

            // Build path: uploadDir/YYYY-MM-DD/
            Path uploadPath = Paths.get(uploadDir)
                    .resolve(today.toString());

            // Create directories if they don't exist (createDirectories = parents too)
            Files.createDirectories(uploadPath);

            return uploadPath;

        } catch (IOException e) {
            throw new FileStorageException(
                    "Failed to create upload directory: " + e.getMessage(), e);
        }
    }

    /**
     * Extract file extension from a filename.
     *
     * Examples:
     *   - "document.pdf" → "pdf"
     *   - "image.jpg" → "jpg"
     *   - "archive.tar.gz" → "gz" (takes only last extension)
     *
     * @param fileName The original filename
     * @return File extension in lowercase (without the dot)
     */
    private String extractFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }
        // Get everything after the last dot
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Convert FileRecord entity to FileRecordDTO.
     * Used to hide sensitive database details and present clean API response.
     *
     * @param fileRecord The FileRecord entity
     * @return FileRecordDTO ready for client consumption
     */
    private FileRecordDTO mapToDTO(FileRecord fileRecord) {
        return FileRecordDTO.builder()
                .fileId(fileRecord.getFileId())
                .fileName(fileRecord.getFileName())
                .fileSize(fileRecord.getFileSize())
                .mimeType(fileRecord.getMimeType())
                .fileType(fileRecord.getFileType())
                .uploadedBy(fileRecord.getUploadedBy())
                .uploadedAt(fileRecord.getUploadedAt())
                .active(fileRecord.getActive())
                .build();
    }
}