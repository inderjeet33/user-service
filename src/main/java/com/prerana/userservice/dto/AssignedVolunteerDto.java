package com.prerana.userservice.dto;

import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.VolunteerOfferStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignedVolunteerDto {

    // Assignment info
    private Long assignmentId;
    private AssignmentStatus assignmentStatus;
    private LocalDateTime assignedAt;

    // Volunteer request
    private Long volunteerRequestId;
    private String volunteerType;
    private String availability;
    private String skills;
    private String location;
    private String preferredContact;
    private String reason;
    private VolunteerOfferStatus status;
    private LocalDateTime requestCreatedAt;

    // Volunteer (individual)
    private Long userId;
    private String userName;
    private String userEmail;
    private String userMobile;
}
