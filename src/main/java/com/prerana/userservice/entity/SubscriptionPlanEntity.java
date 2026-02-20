package com.prerana.userservice.entity;

import com.prerana.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_plans",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"code", "user_type"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanEntity extends BaseEntity {

    private String code;   // FREE, SUPPORTER, CHAMPION

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType; // INDIVIDUAL (later NGO, CSR)

    private Long price; // monthly price (0 for FREE)

    private Integer durationDays;

    @Column(nullable = false)
    private boolean active;

    private Integer priority;
}
