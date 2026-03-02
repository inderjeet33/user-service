package com.prerana.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VolunteerCertificateDto extends BaseDto{

    private String certificateId;
    private String volunteerName;
    private String ngoName;
    private String volunteerType;
    private String completedDate;
    private String platformName;

}
