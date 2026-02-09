package com.prerana.userservice.dto;

import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.HelpRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignedHelpRequestDto {

    // Assignment
    private Long assignmentId;
    private AssignmentStatus assignmentStatus;
    private LocalDateTime assignedAt;

    // Help request
    private Long helpRequestId;
    private String donationCategory;
    private String helpType;
    private Long amount;
    private String itemDetails;
    private Integer quantity;
    private String urgency;
    private String location;
    private String preferredContact;
    private String reason;
    private HelpRequestStatus helpRequestStatus;

    // Requester (person who asked for help)
    private Long requesterId;
    private String requesterName;
    private String requesterMobile;
}
