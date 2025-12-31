package com.prerana.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DonationCertificateDto {

    private String certificateId;

    private String donorName;
    private String donorEmail;

    private String ngoName;
    private String ngoRegistrationNumber;

    private String donationCategory;
    private Long amount;
    private String location;

    private String completedDate;

    private String platformName;

    private Long donationOfferId;
}

