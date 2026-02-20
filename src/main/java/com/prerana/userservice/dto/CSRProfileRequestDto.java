package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class CSRProfileRequestDto {

    // ---- Company Identity ----
    private String companyName;
    private String legalCompanyName;
    private String cinNumber;
    private String gstNumber;
    private String panNumber;

    // ---- Authorized Person ----
    private String authorizedPersonName;
    private String authorizedPersonDesignation;
    private String authorizedPersonEmail;
    private String authorizedPersonPhone;

    // ---- Contact ----
    private String officialEmail;
    private String officialPhone;
    private String website;

    // ---- Address ----
    private String address;
    private String city;
    private String district;
    private String state;
    private String pincode;

    // ---- CSR Specific ----
    private String csrFocusAreas;
    private Long annualCsrBudget;
    private String csrPolicyUrl;

    // ---- Documents ----
    private String documentsJson;
}
