package com.prerana.userservice.service;

import com.prerana.userservice.certificate.CertificatePdfGenerator;
import com.prerana.userservice.dto.AssignedOfferDto;
import com.prerana.userservice.dto.NgoProfile;
import com.prerana.userservice.dto.NgoProfileRequestDto;
import com.prerana.userservice.dto.RejectNgoRequest;
import com.prerana.userservice.entity.*;
import com.prerana.userservice.enums.ActivationStatus;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.UserType;
//import com.prerana.userservice.mapper.MapperUtil;
import com.prerana.userservice.exceptions.MobileNumberOTPNotVerified;
import com.prerana.userservice.exceptions.RejectionReasonMissingException;
import com.prerana.userservice.mapper.NgoMapper;
import com.prerana.userservice.repository.*;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NGOProfileService {
    @Autowired
    private NGOProfileRepository ngoRepo;

    @Autowired
    private NgoMapper mapperUtil;

    @Autowired private UserRepository userRepo;
//    @Autowired private FileStorageService fileStorageService; // handle file uploads

    @Autowired
    private  ModeratorAssignmentRepository assignmentRepository;

    @Autowired
    private CertificateRepository certificateRepo;

    @Autowired
    private DonationOfferRepository donationOfferRepository;

    private void validateNgoStatusChange(AssignmentStatus oldStatus, AssignmentStatus newStatus) {

        if (newStatus == AssignmentStatus.IN_PROGRESS &&
                oldStatus == AssignmentStatus.ASSIGNED) return;

        if (newStatus == AssignmentStatus.COMPLETED &&
                oldStatus == AssignmentStatus.IN_PROGRESS) return;

        if (newStatus == AssignmentStatus.REJECTED_BY_RECEIVER &&
                (oldStatus == AssignmentStatus.ASSIGNED || oldStatus == AssignmentStatus.IN_PROGRESS)) return;

        throw new RuntimeException("Invalid status transition");
    }
    public List<AssignedOfferDto> getAssignedOffers(Long ngoUserId) {

        List<ModeratorAssignmentEntity> assignments =
                assignmentRepository.findAllAssignmentsForNgo(ngoUserId);

        return assignments.stream()
                .map(a -> {
                    DonationOfferEntity offer = a.getDonationRequest();
                    AssignedOfferDto dto = new AssignedOfferDto();

                    dto.setAmount(offer.getAmount());
                    dto.setAssignmentId(a.getId());
                    dto.setDonationOfferId(offer.getId());
                    dto.setReason(offer.getReason());
                    dto.setDonationCategory(offer.getDonationCategory());
                    dto.setLocation(offer.getLocation());
                    dto.setDonorName(offer.getUser().getFullName());
                    dto.setDonorPhone(offer.getUser().getMobileNumber());
                    dto.setAssignmentStatus(a.getStatus());
                    dto.setAssignedAt(a.getCreatedAt());
                    dto.setOfferCreatedAt(offer.getCreatedAt());
                    dto.setTimeLine(offer.getTimeLine());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public NGOProfileEntity completeProfile(Long userId, NgoProfileRequestDto req) {
        UserEntity user = userRepo.findById(userId).orElseThrow();
        if (user.getUserType() != UserType.NGO) {
            throw new RuntimeException("Only NGO users can complete NGO profile");
        }
        NGOProfileEntity profile = ngoRepo.findByUserId(userId).orElse(new NGOProfileEntity());
        profile.setRegistrationType(req.getRegistrationType());
        profile.setEmail(req.getEmail());
        profile.setPhone(req.getPhone());
        profile.setAddress(req.getAddress());
        profile.setAccountHolderName(req.getAccountHolderName());
        profile.setBankName(req.getBankName());
//        profile.setDocumentsJson(String.join(",",req.getDocuments()));
        profile.setUser(user);
        profile.setNgoName(req.getNgoName());
        profile.setRegistrationNumber(req.getRegistrationNumber());
        profile.setBankAccount(req.getBankAccount());
        profile.setIfsc(req.getIfsc());
//        profile.setAddress(req.getAddress());
        profile.setRejectedAt(null);
        profile.setRejectionReason(null);
        profile.setCity(req.getCity());
        profile.setState(req.getState());
        profile.setPincode(req.getPincode());
        profile.setDescription(req.getDescription());
        profile.setCategories(String.join(",", req.getCategories()));
        profile.setDistrict(req.getDistrict());
        // handle docs upload -> get list of urls
//        if (docs != null && docs.length > 0) {
////            List<String> urls = Arrays.stream(docs)
//////                    .map(fileStorageService::storeFile) // storeFile returns URL or path
////                    .collect(Collectors.toList());
////            profile.setDocumentsJson(new ObjectMapper().writeValueAsString(urls));
//        }
        profile.setActivationStatus(ActivationStatus.PENDING);
        profile = ngoRepo.save(profile);

        user.setProfileCompleted(true);
        userRepo.save(user);

        return profile;
    }

    @Transactional
    public void verifyNgo(Long ngoId) {
        NGOProfileEntity ngo = ngoRepo.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        ngo.setActivationStatus(ActivationStatus.VERIFIED);
        ngoRepo.save(ngo);
    }

    @Transactional
    public void rejectNgo(Long ngoId, String reason) {
        NGOProfileEntity ngo = ngoRepo.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        if(StringUtils.isBlank(reason)){
            throw new RejectionReasonMissingException("Kindly provide a valid reason to reject the ngo profile");
        }
        ngo.setRejectionReason(reason);
        ngo.setRejectedAt(LocalDateTime.now());
        ngo.setActivationStatus(ActivationStatus.REJECTED);
        ngoRepo.save(ngo);
    }


    @Transactional
    public String updateAssignedOfferStatus(Long ngoId, Long assignmentId, AssignmentStatus newStatus) {

        ModeratorAssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getReceiver().getId().equals(ngoId)) {
            throw new RuntimeException("Not allowed");
        }
        if (!assignment.isActive()) {
            throw new RuntimeException("Cannot update historical assignment");
        }

        // Validate allowed transitions
        validateNgoStatusChange(assignment.getStatus(), newStatus);

        // Update assignment status
        assignment.setStatus(newStatus);

        // Update donation offer status automatically
        DonationOfferEntity offer = assignment.getDonationRequest();
        offer.setStatus(mapToDonationOfferStatus(newStatus));

        donationOfferRepository.save(offer);
        assignmentRepository.save(assignment);
        if(AssignmentStatus.COMPLETED == newStatus){
            completeAssignment(offer.getAmount(),assignment);
        }

        return "Status updated successfully";
    }

    public void completeAssignment(Long offerAmount,ModeratorAssignmentEntity assignmentEntity) {
        // 🔐 Create certificate ONLY ONCE
        if (!certificateRepo.existsByAssignment_Id(assignmentEntity.getId())) {

            DonationCertificateEntity cert =
                    DonationCertificateEntity.builder()
                            .assignment(assignmentEntity)
                            .donor(assignmentEntity.getDonor())
                            .receiver(assignmentEntity.getReceiver())
                            .donationAmount(offerAmount)
                            .issuedDate(LocalDate.now())
                            .build();

            cert = certificateRepo.save(cert);

            cert.setCertificateId(CertificatePdfGenerator.generateCertificateId(cert.getId()));
            certificateRepo.save(cert);
        }
    }

    private DonationOfferStatus mapToDonationOfferStatus(AssignmentStatus status) {

        switch (status) {
            case IN_PROGRESS:
                return DonationOfferStatus.IN_PROGRESS;

            case COMPLETED:
                return DonationOfferStatus.COMPLETED;

            case REJECTED_BY_RECEIVER:
                return DonationOfferStatus.OPEN;

            default:
                return DonationOfferStatus.ASSIGNED;
        }
    }

    public Optional<NgoProfile> getProfileByUserId(Long userId) {
        Optional<NGOProfileEntity> entity =  ngoRepo.findByUserId(userId);
        if(entity.isPresent()){
            NgoProfile profileDto = mapperUtil.toDto(entity.get());
            if(StringUtils.isNotEmpty(entity.get().getCategories())) {
                profileDto.setCategories(Arrays.asList(entity.get().getCategories().split(",")));
            }
            return Optional.of(mapperUtil.toDto(entity.get()));
        }else{
            return Optional.empty();
        }
    }

    public Page<NgoProfile> search(String city,String state, String category,Boolean verified, int page, int size) {

        String cityFilter = normalize(city);
        String categoryFilter = normalize(category);
        String stateFilter = normalize(state);
        String verifiedFilter = null;
        if(Objects.nonNull(verified)){
            if(verified) {
                verifiedFilter = ActivationStatus.VERIFIED.name();
            }else{
                verifiedFilter = ActivationStatus.REJECTED.name();
            }
        }
        verifiedFilter = normalize(verifiedFilter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<NGOProfileEntity> pageEntities = ngoRepo.search(
                cityFilter,stateFilter, categoryFilter,verifiedFilter, pageable
        );
        Page<NgoProfile> pageDtos = mapperUtil.toDtoPage(pageEntities);
        return pageDtos;
    }

    private String normalize(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    public List<NGOProfileEntity> searchNgos(String category,String city)throws BadRequestException {

        if (StringUtils.isEmpty(city) && StringUtils.isEmpty(category)){
            throw new BadRequestException("Please provide atleast one parameter for querying NGOS");
        }
        else if (StringUtils.isEmpty(category)) {
            return ngoRepo.findByCityContaining(city);
        } else if (StringUtils.isEmpty(city)) {
            return ngoRepo.findByCategoriesContaining(category);
        } else {
            return ngoRepo.findByCityAndCategoriesContaining(city, category);
        }
    }
}
