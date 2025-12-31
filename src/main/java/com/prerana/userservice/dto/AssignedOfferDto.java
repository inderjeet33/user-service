package com.prerana.userservice.dto;

import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignedOfferDto {
    private Long assignmentId;
    private Long donationOfferId;
    private String donorName;
    private String donorPhone;
    private String donationCategory;
    private Long amount;
    private String reason;
    private String timeLine;
    private AssignmentStatus assignmentStatus;
    private LocalDateTime assignedAt;
    private LocalDateTime offerCreatedAt;
    private String location;
}
