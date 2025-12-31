package com.prerana.userservice.dto;

import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModeratorAssignmentDto {
    Long id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserDto moderator;

    UserDto receiver;

    UserType receiver_type;

    UserDto donor;

    AssignmentStatus status;

    DonationOffersRequestDto donationRequest;
}
