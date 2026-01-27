package com.prerana.userservice.dto;

import com.prerana.userservice.enums.HelpRequestStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpRequestResponseDto extends BaseDto {

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

    // Requester (Individual)
    private Long userId;
    private String userName;
    private String userEmail;
    private String userMobile;
}
