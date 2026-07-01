package com.bugai.fileservice.controller;

import com.bugai.fileservice.dto.FileRecordDTO;
import com.bugai.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * FileController exposes REST endpoints for file operations.
 *
 * Endpoints:
 *   POST   /api/files/upload          - Upload a new file
 *   GET    /api/files/{fileId}        - Download file
 *   GET    /api/files/{fileId}/meta   - Get file metadata
 *   DELETE /api/files/{fileId}        - Delete a file (soft delete)
 *   GET    /api/files/user/{userId}   - List files by user
 *
 * Port: 8086
 *
 * Base Path: http://localhost:8086/api/files
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    // Inject the FileService for business logic
    private final FileService fileService;

    /**
     * Upload a new file.
     *
     * Accepts multipart/form-data with:
     *   - file: The actual file content (required)
     *   - uploadedBy: User ID (UUID) uploading the file (required)
     *
     * Validation:
     *   - File cannot be null or empty
     *   - File size must not exceed FileType limit
     *   - uploadedBy must be a valid UUID
     *
     * Response (201 Created):
     *   {
     *     "fileId": "a1b2c3d4-e5f6-4g7h-8i9j-0k1l2m3n4o5p",
     *     "fileName": "bug-report.pdf",
     *     "fileSize": 1024000,
     *     "mimeType": "application/pdf",
     *     "fileType": "DOCUMENT",
     *     "uploadedBy": "user-uuid-here",
     *     "uploadedAt": "2024-12-20T14:30:00",
     *     "active": true
     *   }
     *
     * @param file The file to upload (multipart/form-data)
     * @param uploadedBy User ID uploading the file (query parameter)
     * @return ResponseEntity with 201 status and FileRecordDTO
     */
    @PostMapping("/upload")
    public ResponseEntity<FileRecordDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy) {

        // Delegate to service
        FileRecordDTO uploadedFileDTO = fileService.uploadFile(file, uploadedBy);

        // Return 201 Created with file metadata
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(uploadedFileDTO);
    }

    /**
     * Download a file by its ID.
     *
     * Returns the actual file content as a downloadable resource.
     * Browser will treat as attachment and prompt user to save.
     *
     * Response (200 OK):
     *   [Binary file content]
     *   Headers:
     *     Content-Disposition: attachment; filename="original-filename.pdf"
     *     Content-Type: application/pdf (or appropriate MIME type)
     *
     * @param fileId The file's UUID
     * @return ResponseEntity with file content and download headers
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {

        // Get file metadata to access original filename
        FileRecordDTO fileMetadata = fileService.getFileMetadata(fileId);

        // Get filesystem path from service
        String filePath = fileService.getFilePath(fileId);

        // Create a Resource from the file path
        Resource fileResource = new FileSystemResource(filePath);

        // Check if file exists on disk
        if (!fileResource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Encode filename for HTTP header (handles special characters)
        String encodedFileName = URLEncoder.encode(
                fileMetadata.getFileName(), StandardCharsets.UTF_8);

        // Return file with download headers
        return ResponseEntity.ok()
                // Set Content-Type based on stored MIME type
                .contentType(MediaType.parseMediaType(fileMetadata.getMimeType()))
                // Tell browser to download as attachment with original filename
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"")
                .body(fileResource);
    }

    /**
     * Get file metadata without downloading the actual file content.
     * Useful for checking file size, type, owner before downloading.
     *
     * Response (200 OK):
     *   {
     *     "fileId": "a1b2c3d4-e5f6-4g7h-8i9j-0k1l2m3n4o5p",
     *     "fileName": "bug-report.pdf",
     *     "fileSize": 1024000,
     *     "mimeType": "application/pdf",
     *     "fileType": "DOCUMENT",
     *     "uploadedBy": "user-uuid-here",
     *     "uploadedAt": "2024-12-20T14:30:00",
     *     "active": true
     *   }
     *
     * @param fileId The file's UUID
     * @return ResponseEntity with FileRecordDTO containing metadata
     */
    @GetMapping("/{fileId}/meta")
    public ResponseEntity<FileRecordDTO> getFileMetadata(@PathVariable String fileId) {

        // Retrieve metadata from service
        FileRecordDTO fileMetadata = fileService.getFileMetadata(fileId);

        return ResponseEntity.ok(fileMetadata);
    }

    /**
     * Soft-delete a file (mark as inactive).
     * File is not permanently removed; can be recovered if needed.
     *
     * Response (204 No Content):
     *   [No body; just status code]
     *
     * @param fileId The file's UUID
     * @return ResponseEntity with 204 status
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) {

        // Mark file as inactive in the service
        fileService.deleteFile(fileId);

        // Return 204 No Content (standard for successful DELETE)
        return ResponseEntity.noContent().build();
    }

    /**
     * List all active files uploaded by a specific user.
     * Useful for user dashboard or file management interfaces.
     *
     * Response (200 OK):
     *   {
     *     "files": [
     *       {
     *         "fileId": "a1b2c3d4-e5f6-4g7h-8i9j-0k1l2m3n4o5p",
     *         "fileName": "bug-report.pdf",
     *         ...
     *       },
     *       {
     *         "fileId": "b2c3d4e5-f6a7-4h8i-9j0k-1l2m3n4o5p6q",
     *         "fileName": "screenshot.png",
     *         ...
     *       }
     *     ]
     *   }
     *
     * @param uploadedBy User ID (UUID) to list files for
     * @return ResponseEntity with list of FileRecordDTOs
     */
    @GetMapping("/user/{uploadedBy}")
    public ResponseEntity<Map<String, Object>> getFilesByUser(
            @PathVariable String uploadedBy) {

        // Get files from service
        List<FileRecordDTO> files = fileService.getFilesByUser(uploadedBy);

        // Build response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("files", files);
        response.put("count", files.size());

        return ResponseEntity.ok(response);
    }
}