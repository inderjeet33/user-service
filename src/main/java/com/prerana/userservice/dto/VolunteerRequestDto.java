package com.prerana.userservice.dto;

import com.prerana.userservice.enums.VolunteerType;
import lombok.Data;

@Data
public class VolunteerRequestDto {

    private VolunteerType volunteerType;
    private String skills;
    private String availability;
    private String timeLine;
    private String location;
    private String preferredContact;
    private String reason;
}
