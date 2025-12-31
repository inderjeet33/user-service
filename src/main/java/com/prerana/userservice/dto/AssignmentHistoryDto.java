package com.prerana.userservice.dto;

import com.prerana.userservice.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentHistoryDto {
    private Long id;
    private String receiverName;
    private AssignmentStatus status;
    private LocalDateTime createdAt;

}
