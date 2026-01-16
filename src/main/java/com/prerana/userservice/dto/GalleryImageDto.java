package com.prerana.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class GalleryImageDto extends BaseDto {
    private Long id;
    private String imageUrl;
    private String caption;
    private String ngoName;
    private String status;        // PENDING / APPROVED / REJECTED
    private String rejectReason;
}
