package com.prerana.userservice.entity;

import com.prerana.userservice.enums.CampaignStatus;
import com.prerana.userservice.enums.OwnerType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerType ownerType; // NGO, INDIVIDUAL, etc.

    private String urgency = "MEDIUM";
    private String title;
    private String description;
    private String category; // optional
    private Double targetAmount;
    private LocalDateTime deadline;

    private Double raisedAmount = 0.0;


    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status = CampaignStatus.ACTIVE; // default when created


    private String mediaUrls; // JSON array of images/videos
    private String imageUrl;

    private String beneficiaryType;
    private Integer beneficiaryCount;
}
