package com.bugai.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for analytics queries.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponseDTO {

    private String id;
    private String projectId;
    private Long totalBugs;
    private Long openBugs;
    private Long closedBugs;
    private Double avgResolutionTime;
    private Long criticalBugs;
    private Long highBugs;
    private Long mediumBugs;
    private Long lowBugs;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}