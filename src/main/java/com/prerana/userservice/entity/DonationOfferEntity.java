package com.prerana.userservice.entity;

import com.prerana.userservice.enums.DonationCategory;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.HelpType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "donation_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationOfferEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HelpType helpType;

    @Enumerated(EnumType.STRING)
    private DonationCategory donationCategory;

    private Long amount;          // For FINANCIAL
    private String itemDetails;   // For non-financial
    private String otherDetails;
    private Integer quantity;

    @Column(name = "time_line")
    private String timeLine;

    private boolean recurringHelp;
    private String reason;
    private String ageGroup;
    private String gender;
    private String location;
    private String preferredContact;

    private String type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationOfferStatus status = DonationOfferStatus.OPEN;
}
