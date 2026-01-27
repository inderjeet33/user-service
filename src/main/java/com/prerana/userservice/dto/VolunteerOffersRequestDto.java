package com.prerana.userservice.dto;

import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.VolunteerOfferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerOffersRequestDto extends BaseDto {

    private Long id;

    private UserDto user;
    private String volunteerType;
    private String availability;
    private String skills;
    private String location;
    private String preferredContact;
    private String reason;

    private VolunteerOfferStatus status; // OPEN, ASSIGNED, COMPLETED, CANCELLED
    private AssignmentStatus assignmentStatus;

    // Donor (Volunteer)
    private Long userId;
    private String userName;
    private String userEmail;
    private String userMobile;

    // Assigned NGO
    private String receiverName;
    private Long receiverId;
    private String receiverType;
    private String receiverMobile;
    private String receiverEmail;
    private String receiverCity;
}
