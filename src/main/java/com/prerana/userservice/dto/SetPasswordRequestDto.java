package com.prerana.userservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SetPasswordRequestDto {
    private String mobileNumber;
    private String password;
}
