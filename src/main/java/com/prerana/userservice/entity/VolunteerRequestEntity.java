package com.prerana.userservice.entity;

import com.prerana.userservice.enums.VolunteerOfferStatus;
import com.prerana.userservice.enums.VolunteerType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "volunteer_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerRequestEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;   // Individual who wants to volunteer

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VolunteerType volunteerType;   // TEACHING, CLEANING, etc.

    private String skills;        // e.g. "First Aid, Teaching"
    private String availability; // e.g. "Weekends", "Evenings"

    @Column(name = "time_line")
    private String timeLine;     // Similar to DonationOfferEntity

    private String location;
    private String preferredContact;

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VolunteerOfferStatus  status;
    // OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
}
