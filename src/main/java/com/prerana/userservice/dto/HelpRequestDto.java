package com.prerana.userservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpRequestDto {

    private String requesterType; // "NGO" or "INDIVIDUAL"

    private String category;      // health, education, food, financial

    private String reason;        // description of need
    private String urgency;       // low, medium, high

    private Long amountRequired;  // if financial

    private String typeOfHelp;    // books, clothes, medical aid, volunteer help, etc

    private String ageGroup;      // child, adult, senior or "any"
    private String gender;        // male/female/any
    private String location;      // city, district etc
}
