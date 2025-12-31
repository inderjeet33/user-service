package com.prerana.userservice.entity;

import com.prerana.userservice.enums.Role;
import com.prerana.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    @Column(nullable = false)
    private Boolean isVerified = false;

    // Primary user type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;   // INDIVIDUAL / CORPORATE / NGO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean profileCompleted;

    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String profession;
}
