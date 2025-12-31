package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class OfferStatusRequest {
    private Long offerId;
    private String status; // ACCEPTED / DECLINED / COMPLETED
}
