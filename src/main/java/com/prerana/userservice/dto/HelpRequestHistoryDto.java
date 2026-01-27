package com.prerana.userservice.dto;

import com.prerana.userservice.enums.HelpRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HelpRequestHistoryDto {

    private Long id;

    private String donationCategory;
    private String helpType;

    private Long amount;
    private String itemDetails;
    private Integer quantity;

    private String urgency;
    private String location;
    private String preferredContact;
    private String reason;

    private HelpRequestStatus status;
    private LocalDateTime createdAt;
}
