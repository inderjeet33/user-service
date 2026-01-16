package com.prerana.userservice.entity;

import com.prerana.userservice.enums.ActivationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Table(name = "ngo_profiles")
@Data
public class NGOProfileEntity extends BaseEntity{



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    private String ngoName;
    private String registrationNumber;
    private String registrationType; // TRUST / SOCIETY / SECTION8
    private String email;
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;
    private String city;
    private String state;
    private String pincode;

    private String accountHolderName;
    private String bankAccount;
    private String ifsc;
    private String bankName;

    @Column(columnDefinition = "TEXT")
    private String description;

    // store categories CSV
    private String categories;

    // JSON list of documents
    @Column(columnDefinition = "TEXT")
    private String documentsJson;

    private String district;

    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus = ActivationStatus.PENDING;

    @Column(length = 500)
    private String rejectionReason;

    @Column
    private LocalDateTime rejectedAt;


}
