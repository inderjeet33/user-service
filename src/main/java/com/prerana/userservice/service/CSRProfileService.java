//package com.prerana.userservice.service;
//
//import com.prerana.userservice.entity.CSRProfileEntity;
//import com.prerana.userservice.entity.UserEntity;
//import com.prerana.userservice.repository.CSRProfileRepository;
//import com.prerana.userservice.repository.CsrProfileRepository;
//import com.prerana.userservice.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CsrProfileService {
//
//    @Autowired
//    private UserRepository userRepo;
//
//    @Autowired
//    private CSRProfileRepository csrProfileRepo;
//
//    @Transactional
//    public void completeProfile(Long userId, CSRProfileRequestDto dto) {
//
//        UserEntity user = userRepo.findById(userId).orElseThrow();
//
//        CSRProfileEntity profile =
//                csrProfileRepo.findByUserId(userId)
//                        .orElse(new CSRProfileEntity());
//
//        profile.setUser(user);
//        profile.setCompanyName(dto.getCompanyName());
//        profile.setLegalCompanyName(dto.getLegalCompanyName());
//        profile.setCinNumber(dto.getCinNumber());
//        profile.setGstNumber(dto.getGstNumber());
//        profile.setPanNumber(dto.getPanNumber());
//
//        profile.setAuthorizedPersonName(dto.getAuthorizedPersonName());
//        profile.setAuthorizedPersonDesignation(dto.getAuthorizedPersonDesignation());
//        profile.setAuthorizedPersonEmail(dto.getAuthorizedPersonEmail());
//        profile.setAuthorizedPersonPhone(dto.getAuthorizedPersonPhone());
//
//        profile.setOfficialEmail(dto.getOfficialEmail());
//        profile.setOfficialPhone(dto.getOfficialPhone());
//        profile.setWebsite(dto.getWebsite());
//
//        profile.setAddress(dto.getAddress());
//        profile.setCity(dto.getCity());
//        profile.setDistrict(dto.getDistrict());
//        profile.setState(dto.getState());
//        profile.setPincode(dto.getPincode());
//
//        profile.setCsrFocusAreas(dto.getCsrFocusAreas());
//        profile.setAnnualCsrBudget(dto.getAnnualCsrBudget());
//        profile.setCsrPolicyUrl(dto.getCsrPolicyUrl());
//
//        profile.setActivationStatus(ActivationStatus.PENDING);
//        profile.setRejectedAt(null);
//        profile.setRejectionReason(null);
//
//        csrProfileRepo.save(profile);
//
//        user.setProfileCompleted(true);
//        userRepo.save(user);
//    }
//
//}
package com.prerana.userservice.service;

import com.prerana.userservice.dto.CSRProfileRequestDto;
import com.prerana.userservice.dto.CSRProfileResponseDto;
import com.prerana.userservice.dto.CsrProfileDto;
import com.prerana.userservice.entity.CSRProfileEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.ActivationStatus;
import com.prerana.userservice.repository.CSRProfileRepository;
import com.prerana.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CSRProfileService {

    @Autowired
    private  CSRProfileRepository csrProfileRepo;


    @Autowired
    private  UserRepository userRepo;

    public List<CsrProfileDto> getAllForModerator() {
        return csrProfileRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void approve(Long csrProfileId) {
        CSRProfileEntity csr = csrProfileRepo.findById(csrProfileId)
                .orElseThrow(() -> new RuntimeException("CSR profile not found"));

        csr.setActivationStatus(ActivationStatus.VERIFIED);
        csr.setVerifiedAt(LocalDateTime.now());
        csr.setRejectionReason(null);

        csrProfileRepo.save(csr);
    }

    @Transactional
    public void reject(Long csrProfileId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("Rejection reason required");
        }

        CSRProfileEntity csr = csrProfileRepo.findById(csrProfileId)
                .orElseThrow(() -> new RuntimeException("CSR profile not found"));

        csr.setActivationStatus(ActivationStatus.REJECTED);
        csr.setRejectedAt(LocalDateTime.now());
        csr.setRejectionReason(reason);

        csrProfileRepo.save(csr);
    }

    /* ========================= */

    private CsrProfileDto toDto(CSRProfileEntity e) {
        CsrProfileDto d = new CsrProfileDto();

        d.setId(e.getId());
        d.setUserId(e.getUser().getId());

        d.setCompanyName(e.getCompanyName());
        d.setLegalCompanyName(e.getLegalCompanyName());
        d.setCinNumber(e.getCinNumber());
        d.setGstNumber(e.getGstNumber());
        d.setPanNumber(e.getPanNumber());

        d.setAuthorizedPersonName(e.getAuthorizedPersonName());
        d.setAuthorizedPersonDesignation(e.getAuthorizedPersonDesignation());
        d.setAuthorizedPersonEmail(e.getAuthorizedPersonEmail());
        d.setAuthorizedPersonPhone(e.getAuthorizedPersonPhone());

        d.setOfficialEmail(e.getOfficialEmail());
        d.setOfficialPhone(e.getOfficialPhone());
        d.setWebsite(e.getWebsite());

        d.setCity(e.getCity());
        d.setState(e.getState());

        d.setCsrFocusAreas(e.getCsrFocusAreas());
        d.setAnnualCsrBudget(e.getAnnualCsrBudget());

        d.setActivationStatus(e.getActivationStatus());
        d.setRejectionReason(e.getRejectionReason());

        return d;
    }

    @Transactional
    public void completeProfile(Long userId, CSRProfileRequestDto dto) {

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CSRProfileEntity profile =
                csrProfileRepo.findByUserId(userId)
                        .orElse(CSRProfileEntity.builder().user(user).build());

        profile.setCompanyName(dto.getCompanyName());
        profile.setLegalCompanyName(dto.getLegalCompanyName());
        profile.setCinNumber(dto.getCinNumber());
        profile.setGstNumber(dto.getGstNumber());
        profile.setPanNumber(dto.getPanNumber());

        profile.setAuthorizedPersonName(dto.getAuthorizedPersonName());
        profile.setAuthorizedPersonDesignation(dto.getAuthorizedPersonDesignation());
        profile.setAuthorizedPersonEmail(dto.getAuthorizedPersonEmail());
        profile.setAuthorizedPersonPhone(dto.getAuthorizedPersonPhone());

        profile.setOfficialEmail(dto.getOfficialEmail());
        profile.setOfficialPhone(dto.getOfficialPhone());
        profile.setWebsite(dto.getWebsite());

        profile.setAddress(dto.getAddress());
        profile.setCity(dto.getCity());
        profile.setDistrict(dto.getDistrict());
        profile.setState(dto.getState());
        profile.setPincode(dto.getPincode());

        profile.setCsrFocusAreas(dto.getCsrFocusAreas());
        profile.setAnnualCsrBudget(dto.getAnnualCsrBudget());
        profile.setCsrPolicyUrl(dto.getCsrPolicyUrl());
        profile.setDocumentsJson(dto.getDocumentsJson());

        profile.setActivationStatus(ActivationStatus.PENDING);
        profile.setRejectionReason(null);
        profile.setRejectedAt(null);

        csrProfileRepo.save(profile);

        user.setProfileCompleted(true);
        userRepo.save(user);
    }

    public CSRProfileResponseDto getProfile(Long userId) {

        CSRProfileEntity p = csrProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("CSR profile not found"));

        CSRProfileResponseDto dto = new CSRProfileResponseDto();
        dto.setId(p.getId());
        dto.setCompanyName(p.getCompanyName());
        dto.setLegalCompanyName(p.getLegalCompanyName());
        dto.setCinNumber(p.getCinNumber());
        dto.setGstNumber(p.getGstNumber());
        dto.setPanNumber(p.getPanNumber());

        dto.setAuthorizedPersonName(p.getAuthorizedPersonName());
        dto.setAuthorizedPersonDesignation(p.getAuthorizedPersonDesignation());
        dto.setAuthorizedPersonEmail(p.getAuthorizedPersonEmail());
        dto.setAuthorizedPersonPhone(p.getAuthorizedPersonPhone());

        dto.setOfficialEmail(p.getOfficialEmail());
        dto.setOfficialPhone(p.getOfficialPhone());
        dto.setWebsite(p.getWebsite());

        dto.setAddress(p.getAddress());
        dto.setCity(p.getCity());
        dto.setDistrict(p.getDistrict());
        dto.setState(p.getState());
        dto.setPincode(p.getPincode());

        dto.setCsrFocusAreas(p.getCsrFocusAreas());
        dto.setAnnualCsrBudget(p.getAnnualCsrBudget());
        dto.setCsrPolicyUrl(p.getCsrPolicyUrl());

        dto.setActivationStatus(p.getActivationStatus());
        dto.setRejectionReason(p.getRejectionReason());
        dto.setVerifiedAt(p.getVerifiedAt());

        return dto;
    }
}
