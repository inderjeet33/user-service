package com.prerana.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "donation_certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationCertificateEntity extends BaseEntity {

    @Column(unique = true)
    private String certificateId;   // PRERANA-2025-000123

    @OneToOne
    @JoinColumn(name = "assignment_id", nullable = false, unique = true)
    private ModeratorAssignmentEntity assignment;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private UserEntity donor;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    private Long donationAmount;

    private LocalDate issuedDate;
}
