package com.prerana.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignDto {

    private String title;
    private String description;
    private String category;
    private Double targetAmount;
    private LocalDateTime deadline;
    private String urgency;
    private String city;
    private String state;
    private String address;
    private Double raisedAmount;
}
