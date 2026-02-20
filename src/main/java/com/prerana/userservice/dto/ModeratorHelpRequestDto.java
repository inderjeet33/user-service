package com.prerana.userservice.dto;

import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.HelpRequestStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ModeratorHelpRequestDto extends BaseDto {
    private String donationCategory;
    private String helpType;
    private String urgency;
    private String location;
    private String reason;

    private HelpRequestStatus status;
    private LocalDateTime createdAt;

    // Requester
    private Long requesterId;
    private String requesterName;
    private String requesterMobile;

    // Assignment (optional)
    private Long assignmentId;
    private String helperName;
    private String helperType;
    private AssignmentStatus assignmentStatus;

    private String priority;
}
