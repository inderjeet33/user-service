package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String mobileNumber;
    private String password;
    private String userType;
}
