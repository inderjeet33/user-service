package com.prerana.userservice.dto;

import com.prerana.userservice.enums.CampaignStatus;
import com.prerana.userservice.enums.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignResponseDto extends BaseDto {

    private String title;
    private String description;
    private String category;
    private Double targetAmount;
    private LocalDateTime deadline;
    private String location;
    private String mediaUrls;
    private OwnerType ownerType;
    private String ownerName;
    private Long ownerId;
    private CampaignStatus status;
    private String city;
    private String state;
    private String address;
    private String imageUrl;
    private String mobileNumber;

    private String urgency;
    private String beneficiaryType;
    private Integer beneficiaryCount;
    private Double raisedAmount;

}
