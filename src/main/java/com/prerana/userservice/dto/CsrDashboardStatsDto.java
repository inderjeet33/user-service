package com.prerana.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CsrDashboardStatsDto {

    // Donations
    private Long totalDonations;
    private Long completedDonations;
    private Long activeDonations;
    private Long totalDonationAmount;

    // Help Requests
    private Long helpRequestsAssigned;
    private Long helpRequestsInProgress;
    private Long helpRequestsCompleted;

    // Impact
    private Long ngosWorkedWith;
    private Long livesImpacted; // placeholder / derived later
}

