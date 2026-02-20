package com.prerana.userservice.dto;

import com.prerana.userservice.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class UserSubscriptionDto {

    private String planCode;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
