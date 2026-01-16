package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class SupportQueryRequestDto {
    private String name;
    private String emailOrPhone;
    private String concernType;
    private String message;
}

