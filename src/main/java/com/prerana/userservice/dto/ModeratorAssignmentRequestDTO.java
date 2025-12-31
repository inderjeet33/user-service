package com.prerana.userservice.dto;
import lombok.Data;

@Data
public class ModeratorAssignmentRequestDTO {
    private Long donationRequestId;
    private Long receiverId;
}
