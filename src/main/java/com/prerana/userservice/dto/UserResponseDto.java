package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String mobile;
    private String email;
    private String role;
    private String userType;
    private String profession;
    private String address;
    private String city;
    private String state;
    private String pinCode;
}
