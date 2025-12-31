package com.prerana.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDto {
    private String type;
    private String url;
}
