package com.prerana.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "support_queries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportQueryEntity extends BaseEntity {

    private String name;

    @Column(nullable = false)
    private String emailOrPhone;

    @Column(nullable = false)
    private String type; // INDIVIDUAL / NGO

    @Column(nullable = false)
    private String concernType; // GENERAL, PAYMENT, ASSIGNMENT, OTHER

    @Column(length = 2000, nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userId;

}
