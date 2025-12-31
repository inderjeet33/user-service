package com.prerana.userservice.entity;

import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "moderator_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeratorAssignmentEntity extends BaseEntity {


    // who assigned
    @ManyToOne
    @JoinColumn(name = "moderator_id", nullable = false)
    private UserEntity moderator;

    @ManyToOne
    @JoinColumn(name = "donation_request_id", nullable = false)
    private DonationOfferEntity donationRequest;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType receiver_type;


    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private UserEntity donor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;


    public boolean isActive() {
        return status == AssignmentStatus.ASSIGNED ||
                status == AssignmentStatus.IN_PROGRESS;
    }
}
