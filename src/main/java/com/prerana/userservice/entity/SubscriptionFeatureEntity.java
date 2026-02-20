package com.prerana.userservice.entity;

import com.prerana.userservice.enums.FeatureKey;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_features")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionFeatureEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlanEntity plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeatureKey featureKey;

    @Column(nullable = false)
    private String featureValue;
    // examples:
    // "1", "5", "UNLIMITED", "true", "false"
}
