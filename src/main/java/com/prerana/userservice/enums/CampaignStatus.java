package com.prerana.userservice.enums;

public enum CampaignStatus {
    ACTIVE,       // campaign is ongoing and accepting donations
    COMPLETED,    // campaign target reached or ended
    CANCELLED,    // campaign cancelled by owner
    EXPIRED       // campaign ended without reaching target
}
