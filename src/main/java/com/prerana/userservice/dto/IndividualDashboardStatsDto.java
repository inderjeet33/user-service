package com.prerana.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndividualDashboardStatsDto {
    private long totalDonations;
    private long totalAmount;
    private long activeDonations;
    private long ngosConnected;
}
