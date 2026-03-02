package com.prerana.userservice.dto;

import com.prerana.userservice.enums.ActivationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CsrProfile extends BaseDto{
    private UserDto user;
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
    private String csrFocusAreas;      // CSV or JSON
    private Long annualCsrBudget;
    private String csrPolicyUrl;

    // ---- Documents ----
    private String documentsJson;

    // ---- Moderation ----
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;

    private String rejectionReason;
    private LocalDateTime rejectedAt;
    private LocalDateTime verifiedAt;
}
