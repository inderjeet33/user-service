package com.prerana.userservice.dto;

import lombok.Data;

@Data
public class GalleryImageDto {
    private Long id;
    private String imageUrl;
    private String caption;
    private String ngoName;
}
