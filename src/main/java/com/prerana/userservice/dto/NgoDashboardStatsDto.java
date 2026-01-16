package com.prerana.userservice.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NgoDashboardStatsDto {
    private long totalRequests;
    private long activeRequests;
    private long assignedDonations;
    private long completedDonations;
    private long pendingDonations;
    private Long campaigns;
    private String activationStatus;
    private String rejectedReason;
}
