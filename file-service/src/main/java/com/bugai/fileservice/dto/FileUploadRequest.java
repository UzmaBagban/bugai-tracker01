package com.bugai.fileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileUploadRequest DTO bundles file upload metadata.
 *
 * Usage:
 *   - MultipartFile: The actual file content
 *   - uploadedBy: User ID (from request param or JWT token in Phase 2)
 *   - description: Optional file description/metadata
 *
 * In Spring controllers, this is typically filled via:
 *   @RequestParam("file") MultipartFile file
 *   @RequestParam("uploadedBy") String uploadedBy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {

    /**
     * The actual file content (multipart/form-data).
     * Spring automatically maps @RequestParam("file") to this.
     */
    private MultipartFile file;

    /**
     * User ID (UUID) uploading the file.
     * Currently from request param; will be extracted from JWT in Phase 2.
     */
    private String uploadedBy;

    /**
     * Optional description or metadata about the file.
     * Can be used for search/filtering in future versions.
     */
    private String description;
}