package com.prerana.userservice.dto;

import com.prerana.userservice.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String name;
    private UserType userType;
    private Long userId;
    private String role;
}
