package com.prerana.userservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
public class SubscriptionPlanDto extends BaseDto {

    private String code;
    private Long price;
    private String name;
    private String description;
    private Integer durationDays;

    private Map<String, String> features;
}
