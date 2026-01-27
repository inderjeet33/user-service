package com.prerana.userservice.dto;
import lombok.Data;

@Data
public class ModeratorAssignmentRequestDTO extends BaseDto{
    private Long donationRequestId;
    private Long receiverId;
}
