package com.prerana.userservice.dto;

import com.prerana.userservice.enums.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCampaignDto {
    private String title;
    private String description;
    private String category;
    private Double targetAmount;
    private LocalDateTime deadline;
    private String location;
    private String mediaUrls;
    private OwnerType ownerType;
    private String urgency;
    private String city;
    private String state;
}
