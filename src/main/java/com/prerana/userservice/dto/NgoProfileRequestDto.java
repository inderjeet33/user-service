package com.prerana.userservice.dto;

import jakarta.annotation.Nonnull;
import lombok.Data;

import java.util.List;
@Data
public class NgoProfileRequestDto extends BaseDto{
    @Nonnull
    private String ngoName;
    @Nonnull
    private String registrationNumber;
    private String registrationType; // TRUST / SOCIETY / SECTION8
    @Nonnull
    private String email;
    @Nonnull
    private String district;
    @Nonnull
    private String phone;

    @Nonnull
    private String address;

    @Nonnull
    private String city;

    @Nonnull
    private String state;

    @Nonnull
    private String pincode;

    @Nonnull
    private String accountHolderName;
    @Nonnull
    private String bankAccount;

    @Nonnull
    private String ifsc;

    @Nonnull
    private String bankName;

    @Nonnull
    private String description;

    @Nonnull
    private List<String> categories;

    private List<DocumentDto> documents;
}
