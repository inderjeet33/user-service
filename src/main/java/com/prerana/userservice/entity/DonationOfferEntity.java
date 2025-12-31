package com.prerana.userservice.entity;
import com.prerana.userservice.enums.DonationOfferStatus;
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

    private Long amount;
    private String donationCategory;

    @Column(name = "time_line")
    private String timeLine;
    private boolean recurringHelp;
    private String reason;
    private String type;
    private String ageGroup;
    private String gender;
    private String location;
    private String preferredContact;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationOfferStatus status = DonationOfferStatus.OPEN;
}
