package com.prerana.userservice.entity;

import com.prerana.userservice.enums.ActivationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "csr_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CSRProfileEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

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
