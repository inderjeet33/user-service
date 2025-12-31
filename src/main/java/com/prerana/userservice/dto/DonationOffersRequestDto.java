package com.prerana.userservice.dto;

import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
public class DonationOffersRequestDto extends BaseDto{

    private UserDto user;
    private Long amount;
    private String donationCategory;
    private String timeLine;
    private boolean recurringHelp;
    private String reason;
    private String type;
    private String ageGroup;
    private String gender;
    private String location;
    private String preferredContact;
    private String receiverName;
    private Long receiverId;
    private String receiverType;
    private String receiverMobile;
    private String receiverEmail;
    private String receiverCity;
    private DonationOfferStatus status;
    private AssignmentStatus assignmentStatus;
}
