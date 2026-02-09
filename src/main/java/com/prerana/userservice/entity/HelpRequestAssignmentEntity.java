package com.prerana.userservice.entity;

import com.prerana.userservice.entity.BaseEntity;
import com.prerana.userservice.entity.HelpRequestEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "help_request_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpRequestAssignmentEntity extends BaseEntity {

    // Moderator who assigned
    @ManyToOne
    @JoinColumn(name = "moderator_id", nullable = false)
    private UserEntity moderator;

    // Help request
    @ManyToOne
    @JoinColumn(name = "help_request_id", nullable = false)
    private HelpRequestEntity helpRequest;

    // Who will help (NGO / Individual / CSR later)
    @ManyToOne
    @JoinColumn(name = "helper_id", nullable = false)
    private UserEntity helper;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType helperType; // NGO, INDIVIDUAL, CSR (future)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;
    // ASSIGNED, IN_PROGRESS, COMPLETED, REJECTED_BY_HELPER, CANCELLED
}
