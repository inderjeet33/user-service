package com.prerana.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CampaignUpdateResponseDto {
    private String message;
    private LocalDateTime createdAt;
}
