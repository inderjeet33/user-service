package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class VolunteerAssignmentRequestDTO extends BaseDto{
    private Long volunteerRequestId;
    private Long receiverId;

}
