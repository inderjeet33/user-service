package com.prerana.userservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@Builder
public class ModeratorUserProfileDto extends BaseDto{

    // Basic Info
    private String fullName;
    private String email;
    private String mobileNumber;
    private String userType;
    private String role;
    private Boolean verified;
    private Boolean profileCompleted;
    private String city;
    private String state;
    private String profession;
    private LocalDateTime registeredAt;

    // Donation Stats
    private Long totalDonations;
    private Long activeDonations;
    private Long completedDonations;
    private Long cancelledDonations;

    // Volunteer Stats
    private Long totalVolunteerOffers;
    private Long activeVolunteerOffers;
    private Long completedVolunteerOffers;
    private Long cancelledVolunteerOffers;

    // Help Request Stats
    private Long totalHelpRequests;
    private Long activeHelpRequests;
    private Long completedHelpRequests;
    private Long cancelledHelpRequests;

    // Behaviour indicator
    private Double donationCancellationRate;

    private NgoProfile ngoProfile;
    private CsrProfile csrProfile;
}
