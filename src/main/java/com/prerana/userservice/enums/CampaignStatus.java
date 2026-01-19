package com.prerana.userservice.enums;

public enum CampaignStatus {
    PENDING_FOR_APPROVAL,
    REJECTED,
    ACTIVE,       // campaign is ongoing and accepting donations
    COMPLETED,    // campaign target reached or ended
    CANCELLED,    // campaign cancelled by owner
    EXPIRED  ,     // campaign ended without reaching target
    WITHDRAWN
}
