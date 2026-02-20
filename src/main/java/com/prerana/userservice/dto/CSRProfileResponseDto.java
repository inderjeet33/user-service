package com.prerana.userservice.dto;

import com.prerana.userservice.enums.ActivationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CSRProfileResponseDto {

    private Long id;

    private String companyName;
    private String legalCompanyName;
    private String cinNumber;
    private String gstNumber;
    private String panNumber;

    private String authorizedPersonName;
    private String authorizedPersonDesignation;
    private String authorizedPersonEmail;
    private String authorizedPersonPhone;

    private String officialEmail;
    private String officialPhone;
    private String website;

    private String address;
    private String city;
    private String district;
    private String state;
    private String pincode;

    private String csrFocusAreas;
    private Long annualCsrBudget;
    private String csrPolicyUrl;

    private ActivationStatus activationStatus;
    private String rejectionReason;
    private LocalDateTime verifiedAt;
}
