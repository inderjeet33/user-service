package com.prerana.userservice.dto;

import com.prerana.userservice.enums.Role;
import com.prerana.userservice.enums.UserType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
public class UserDto extends BaseDto{
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String encryptedPassword;

    private boolean isVerified;
    private UserType userType;
    private Role role;
    private boolean profileCompleted;
}
