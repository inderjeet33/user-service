package com.prerana.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
