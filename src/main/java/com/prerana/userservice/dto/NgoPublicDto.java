package com.prerana.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NgoPublicDto {
    private Long id;
    private String name;
    private String email;
    private String mobile;
    private String city;
    private String state;
    private String address;
    private String description;
    private String website;
    private Long ownerId;
}

