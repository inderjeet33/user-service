package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class CampaignPublicDto {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Double targetAmount;
    private Double collectedAmount;
    private String ngoName;
}
