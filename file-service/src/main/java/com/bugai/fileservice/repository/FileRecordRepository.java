package com.bugai.fileservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.bugai.fileservice.entity.FileRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * FileRecordRepository provides database access for FileRecord entities.
 * Extends JpaRepository for CRUD operations + custom queries for file lookups.
 */
@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, String> {

    /**
     * Find a file by ID, only if it's active (not soft-deleted).
     * Used in download and metadata retrieval endpoints.
     *
     * @param fileId The file's UUID
     * @return Optional<FileRecord> containing the file if found and active
     */
    @Query("SELECT f FROM FileRecord f WHERE f.fileId = :fileId AND f.active = true")
    Optional<FileRecord> findByFileIdAndActive(@Param("fileId") String fileId);

    /**
     * Find all active files uploaded by a specific user.
     * Useful for listing a user's uploads or quota management.
     *
     * @param uploadedBy User ID (UUID)
     * @return List of active FileRecords belonging to this user
     */
    @Query("SELECT f FROM FileRecord f WHERE f.uploadedBy = :uploadedBy AND f.active = true")
    List<FileRecord> findByUploadedByAndActive(@Param("uploadedBy") String uploadedBy);

    /**
     * Find all active files uploaded within a date range.
     * Useful for generating usage reports or cleanup tasks.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of active files uploaded in the specified range
     */
    @Query("SELECT f FROM FileRecord f WHERE f.active = true " +
            "AND f.uploadedAt >= :startDate AND f.uploadedAt <= :endDate")
    List<FileRecord> findByDateRangeAndActive(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * Check if a file exists and is active.
     * Lightweight check before attempting download/deletion.
     *
     * @param fileId The file's UUID
     * @return true if file exists and is active, false otherwise
     */
    @Query("SELECT COUNT(f) > 0 FROM FileRecord f WHERE f.fileId = :fileId AND f.active = true")
    boolean existsByFileIdAndActive(@Param("fileId") String fileId);
}