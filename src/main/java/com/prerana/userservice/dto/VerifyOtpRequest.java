package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String mobileNumber;
    private String otp;
}
