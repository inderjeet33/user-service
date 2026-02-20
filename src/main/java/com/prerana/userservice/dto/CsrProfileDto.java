package com.prerana.userservice.dto;


import com.prerana.userservice.enums.ActivationStatus;
import lombok.Data;

@Data
public class CsrProfileDto {

    private Long id;
    private Long userId;

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

    private String city;
    private String state;

    private String csrFocusAreas;
    private Long annualCsrBudget;

    private ActivationStatus activationStatus;
    private String rejectionReason;
}
