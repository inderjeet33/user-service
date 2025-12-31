package com.prerana.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeratorDashboardStatsDto {
    private long totalOffers;
    private long unassignedOffers;
    private long totalNgos;
    private long pendingNgoApprovals;
    private long pendingCampaigns;
    private Long completedOffersToday;
}
