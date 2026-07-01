package com.bugai.analyticsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating analytics records.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsRequestDTO {

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @Positive(message = "Total bugs must be positive")
    private Long totalBugs;

    @Positive(message = "Open bugs must be positive")
    private Long openBugs;

    @Positive(message = "Closed bugs must be positive")
    private Long closedBugs;

    @Positive(message = "Average resolution time must be positive")
    private Double avgResolutionTime;

    private Long criticalBugs;
    private Long highBugs;
    private Long mediumBugs;
    private Long lowBugs;
}