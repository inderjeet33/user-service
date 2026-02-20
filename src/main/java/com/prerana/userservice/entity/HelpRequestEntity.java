package com.prerana.userservice.entity;

import com.prerana.userservice.dto.PriorityLevel;
import com.prerana.userservice.enums.DonationCategory;
import com.prerana.userservice.enums.HelpRequestStatus;
import com.prerana.userservice.enums.HelpType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "help_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpRequestEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;   // Individual asking for help

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationCategory donationCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HelpType helpType;

    // Financial
    private Long amount;

    // Non-financial
    private String itemDetails;
    private Integer quantity;

    private String urgency;     // Immediate / This Week / etc
    private String location;
    private String preferredContact;

    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HelpRequestStatus status = HelpRequestStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priority; // NORMAL, PRIORITY

}
