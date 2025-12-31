package com.prerana.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPasswordRequest {
    private String mobileNumber;
    private String password;
}

