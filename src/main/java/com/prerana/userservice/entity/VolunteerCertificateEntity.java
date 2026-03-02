package com.prerana.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "volunteer_certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerCertificateEntity extends BaseEntity {

    @Column(unique = true)
    private String certificateId;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private VolunteerAssignmentEntity assignment;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private UserEntity volunteer;

    @ManyToOne
    @JoinColumn(name = "volunteered_at",nullable = false)
    private UserEntity volunteeredAt;

    private LocalDate issuedDate;
}