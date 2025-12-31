package com.prerana.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpRequestDto {
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number. It must be 10 digits starting with 6-9")
    private String mobileNumber;

    @NotBlank(message = "User type is required")
    private String userType;

    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email;

}
