package com.prerana.userservice.dto;

import com.prerana.userservice.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HelperDto {
    private Long id;
    private String name;
    private UserType type;
}
