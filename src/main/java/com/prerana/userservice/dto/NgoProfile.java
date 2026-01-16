package com.prerana.userservice.dto;

import com.prerana.userservice.enums.ActivationStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NgoProfile extends BaseDto{
    private UserDto user;

    private String ngoName;
    private String registrationNumber;
    private String pan;
    private String bankAccount;
    private String ifsc;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String description;
    private List<String> categories;
    private ActivationStatus activationStatus;
    private boolean verified;
    private String phone;
    private String accountHolderName;
    private String email;
    private String district;
    private String rejectionReason;
    private LocalDateTime rejectedAt;

    private String bankName;
    // For file uploads, controller will accept MultipartFile[] docs
}
