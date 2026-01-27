package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class HelpRequestCreateDto {

    private String donationCategory;
    private String helpType;

    private Long amount;
    private String itemDetails;
    private Integer quantity;

    private String urgency;
    private String location;
    private String preferredContact;
    private String reason;
}
