package com.prerana.userservice.service;

import com.prerana.userservice.certificate.CertificatePdfGenerator;
import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.*;
import com.prerana.userservice.enums.*;
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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    private VolunteerAssignmentRepository volunteerAssignmentRepository;

    @Autowired
    private CertificateRepository certificateRepo;

    @Autowired
    private DonationOfferRepository donationOfferRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private SubscriptionPlanRepository planRepo;

    @Autowired
    private VolunteerRequestRepository volunteerRequestRepository;


    private void validateNgoStatusChange(AssignmentStatus oldStatus, AssignmentStatus newStatus) {

        if (newStatus == AssignmentStatus.COMPLETED &&
                oldStatus == AssignmentStatus.ASSIGNED) return;

        if ((newStatus == AssignmentStatus.REJECTED_BY_RECEIVER || newStatus == AssignmentStatus.CANCELLED_BY_DONOR)&&
                (oldStatus == AssignmentStatus.ASSIGNED )) return;

        throw new RuntimeException("Invalid status transition");
    }

    public List<AssignedOfferDto> getAssignedOffers(Long ngoUserId, String view) {

        List<DonationOfferStatus> statuses;

        if ("HISTORY".equalsIgnoreCase(view)) {
            statuses = List.of(
                    DonationOfferStatus.COMPLETED,
                    DonationOfferStatus.CANCELLED,
                    DonationOfferStatus.EXPIRED
            );
        } else {
            statuses = List.of(
                    DonationOfferStatus.ASSIGNED,
                    DonationOfferStatus.IN_PROGRESS,
                    DonationOfferStatus.DELIVERED
            );
        }

        List<ModeratorAssignmentEntity> assignments =
                assignmentRepository
                        .findByReceiver_IdAndDonationRequest_StatusInOrderByCreatedAtDesc(
                                ngoUserId, statuses
                        );

        return assignments.stream()
                .map(a -> {
                    DonationOfferEntity offer = a.getDonationRequest();

                    AssignedOfferDto dto = new AssignedOfferDto();
                    dto.setAmount(offer.getAmount());
                    dto.setAssignmentId(a.getId());
                    dto.setDonationOfferId(offer.getId());
                    dto.setReason(offer.getReason());
                    dto.setDonationCategory(offer.getDonationCategory().name());
                    dto.setLocation(offer.getLocation());
                    dto.setDonorName(offer.getUser().getFullName());
                    dto.setDonorPhone(offer.getUser().getMobileNumber());
                    dto.setDonationStatus(offer.getStatus());
                    dto.setAssignmentStatus(a.getStatus());
                    dto.setAssignedAt(a.getCreatedAt());
                    dto.setOfferCreatedAt(offer.getCreatedAt());
                    dto.setTimeLine(offer.getTimeLine());
                    dto.setItemDetails(offer.getItemDetails());
                    dto.setQuantity(offer.getQuantity());
                    dto.setHelpType(offer.getHelpType().name());

                    return dto;
                })
                .toList();
    }
//    public List<AssignedOfferDto> getAssignedOffers(Long ngoUserId) {
//
//        List<ModeratorAssignmentEntity> assignments =
//                assignmentRepository.findAllAssignmentsForNgo(ngoUserId);
//
//        return assignments.stream()
//                .map(a -> {
//                    DonationOfferEntity offer = a.getDonationRequest();
//                    AssignedOfferDto dto = new AssignedOfferDto();
//
//                    dto.setAmount(offer.getAmount());
//                    dto.setAssignmentId(a.getId());
//                    dto.setDonationOfferId(offer.getId());
//                    dto.setReason(offer.getReason());
//                    dto.setDonationCategory(offer.getDonationCategory().name());
//                    dto.setLocation(offer.getLocation());
//                    dto.setDonorName(offer.getUser().getFullName());
//                    dto.setDonorPhone(offer.getUser().getMobileNumber());
//                    dto.setDonationStatus(offer.getStatus());
//                    dto.setAssignmentStatus(a.getStatus());
//                    dto.setAssignedAt(a.getCreatedAt());
//                    dto.setOfferCreatedAt(offer.getCreatedAt());
//                    dto.setTimeLine(offer.getTimeLine());
//                    dto.setItemDetails(offer.getItemDetails());
//                    dto.setQuantity(offer.getQuantity());
//                    dto.setHelpType(offer.getHelpType().name());
//
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }

//    public List<VolunteerOffersRequestDto> getAssignedVolunteers(Long ngoUserId) {
//
//        List<VolunteerAssignmentEntity> assignments =
//                volunteerAssignmentRepository.findAllAssignmentsForNgo(ngoUserId);
//
//        return assignments.stream()
//                .map(a -> {
//                    VolunteerRequestEntity vr = a.getVolunteerRequest();
//                    UserEntity volunteer = a.getVolunteer();
//
//                    VolunteerOffersRequestDto dto = new VolunteerOffersRequestDto();
//
//                    dto.setAssignmentStatus(a.getStatus());
//                    dto.setId(vr.getId());
//
//                    // Volunteer request info
//                    dto.setVolunteerType(vr.getVolunteerType().name());
//                    dto.setAvailability(vr.getAvailability());
//                    dto.setSkills(vr.getSkills());
//                    dto.setLocation(vr.getLocation());
//                    dto.setPreferredContact(vr.getPreferredContact());
//                    dto.setReason(vr.getReason());
//                    dto.setStatus(vr.getStatus());
//
//                    // Volunteer (individual)
//                    dto.setUserId(volunteer.getId());
//                    dto.setUserName(volunteer.getFullName());
//                    dto.setUserEmail(volunteer.getEmail());
//                    dto.setUserMobile(volunteer.getMobileNumber());
//
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }
//
//    public List<AssignedVolunteerDto> getAssignedVolunteers(Long ngoUserId) {
//
//        List<VolunteerAssignmentEntity> assignments =
//                volunteerAssignmentRepository.findAllAssignmentsForNgo(ngoUserId);
//
//        return assignments.stream()
//                .map(a -> {
//                    VolunteerRequestEntity vr = a.getVolunteerRequest();
//                    UserEntity volunteer = a.getVolunteer();
//
//                    AssignedVolunteerDto dto = new AssignedVolunteerDto();
//
//                    // Assignment info
//                    dto.setAssignmentId(a.getId());
//                    dto.setAssignmentStatus(a.getStatus());
//                    dto.setAssignedAt(a.getCreatedAt());
//
//                    // Volunteer request info
//                    dto.setVolunteerRequestId(vr.getId());
//                    dto.setVolunteerType(vr.getVolunteerType().name());
//                    dto.setAvailability(vr.getAvailability());
//                    dto.setSkills(vr.getSkills());
//                    dto.setLocation(vr.getLocation());
//                    dto.setPreferredContact(vr.getPreferredContact());
//                    dto.setReason(vr.getReason());
//                    dto.setStatus(vr.getStatus());
//                    dto.setRequestCreatedAt(vr.getCreatedAt());
//
//                    // Volunteer (individual user)
//                    dto.setUserId(volunteer.getId());
//                    dto.setUserName(volunteer.getFullName());
//                    dto.setUserEmail(volunteer.getEmail());
//                    dto.setUserMobile(volunteer.getMobileNumber());
//
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }

    public List<AssignedVolunteerDto> getAssignedVolunteers(Long ngoUserId, String view) {

        List<AssignmentStatus> statuses;

        if ("HISTORY".equalsIgnoreCase(view)) {
            statuses = List.of(
                    AssignmentStatus.COMPLETED,
                    AssignmentStatus.REJECTED_BY_RECEIVER
            );
        } else {
            statuses = List.of(
                    AssignmentStatus.ASSIGNED,
                    AssignmentStatus.IN_PROGRESS
            );
        }

        List<VolunteerAssignmentEntity> assignments =
                volunteerAssignmentRepository
                        .findByReceiver_IdAndStatusInOrderByCreatedAtDesc(
                                ngoUserId, statuses
                        );

        return assignments.stream()
                .map(a -> {
                    VolunteerRequestEntity vr = a.getVolunteerRequest();
                    UserEntity volunteer = a.getVolunteer();

                    AssignedVolunteerDto dto = new AssignedVolunteerDto();

                    dto.setAssignmentId(a.getId());
                    dto.setAssignmentStatus(a.getStatus());
                    dto.setAssignedAt(a.getCreatedAt());

                    dto.setVolunteerRequestId(vr.getId());
                    dto.setVolunteerType(vr.getVolunteerType().name());
                    dto.setAvailability(vr.getAvailability());
                    dto.setSkills(vr.getSkills());
                    dto.setLocation(vr.getLocation());
                    dto.setPreferredContact(vr.getPreferredContact());
                    dto.setReason(vr.getReason());
                    dto.setStatus(vr.getStatus());
                    dto.setRequestCreatedAt(vr.getCreatedAt());

                    dto.setUserId(volunteer.getId());
                    dto.setUserName(volunteer.getFullName());
                    dto.setUserEmail(volunteer.getEmail());
                    dto.setUserMobile(volunteer.getMobileNumber());

                    return dto;
                })
                .toList();
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

        UserEntity user= ngo.getUser();
        if (userSubscriptionRepository.findFirstByUser_IdAndActiveTrue(user.getId()).isEmpty()) {

            SubscriptionPlanEntity freePlan =
                    planRepo.findByCodeAndUserType("FREE", UserType.NGO)
                            .orElseThrow();

            UserSubscriptionEntity sub = UserSubscriptionEntity.builder()
                    .user(user)
                    .plan(freePlan)
                    .status(SubscriptionStatus.ACTIVE)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(freePlan.getDurationDays()))
                    .active(true)
                    .build();

            userSubscriptionRepository.save(sub);
        }
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
    public String updateDonationProgress(Long ngoId,
                                         Long offerId,
                                         DonationOfferStatus newStatus) {

        DonationOfferEntity offer = donationOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        // Ensure this NGO is assigned
        ModeratorAssignmentEntity assignment =
                assignmentRepository.findTopByDonationRequest_IdAndStatusInOrderByCreatedAtDesc(
                        offerId,
                        List.of(AssignmentStatus.ASSIGNED)
                ).orElseThrow(() -> new RuntimeException("No active assignment"));

        if (!assignment.getReceiver().getId().equals(ngoId)) {
            throw new RuntimeException("Unauthorized");
        }

        validateNgoProgressChange(offer.getStatus(), newStatus);

        offer.setStatus(newStatus);
        donationOfferRepository.save(offer);

        return "Donation status updated successfully";
    }

    private void validateNgoProgressChange(DonationOfferStatus oldStatus,
                                           DonationOfferStatus newStatus) {

        if (newStatus == DonationOfferStatus.IN_PROGRESS &&
                oldStatus == DonationOfferStatus.ASSIGNED) return;

        if (newStatus == DonationOfferStatus.DELIVERED &&
                oldStatus == DonationOfferStatus.IN_PROGRESS) return;

        throw new RuntimeException("Invalid status transition");
    }

//    @Transactional
//    public String updateAssignedOfferStatus(Long ngoId, Long assignmentId, AssignmentStatus newStatus) {
//
//        ModeratorAssignmentEntity assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new RuntimeException("Assignment not found"));
//
//        if (!assignment.getReceiver().getId().equals(ngoId)) {
//            throw new RuntimeException("Not allowed");
//        }
//        if (!assignment.isActive()) {
//            throw new RuntimeException("Cannot update historical assignment");
//        }
//
//        // Validate allowed transitions
//        validateNgoStatusChange(assignment.getStatus(), newStatus);
//
//        // Update assignment status
//        assignment.setStatus(newStatus);
//
//        // Update donation offer status automatically
//        DonationOfferEntity offer = assignment.getDonationRequest();
//        offer.setStatus(mapToDonationOfferStatus(newStatus));
//
//        donationOfferRepository.save(offer);
//
//        assignmentRepository.save(assignment);
//        if(AssignmentStatus.COMPLETED == newStatus){
//            completeAssignment(offer.getAmount(),assignment);
//        }
//
//        return "Status updated successfully";
//    }

//    @Transactional
//    public String updateAssignedOfferStatus(Long ngoId,
//                                            Long assignmentId,
//                                            DonationOfferStatus newStatus) {
//
//        // Fetch assignment
//        ModeratorAssignmentEntity assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new RuntimeException("Assignment not found"));
//
//        // Security check
//        if (!assignment.getReceiver().getId().equals(ngoId)) {
//            throw new RuntimeException("Not allowed");
//        }
//
//        if (!assignment.isActive()) {
//            throw new RuntimeException("Cannot update historical assignment");
//        }
//
//        DonationOfferEntity offer = assignment.getDonationRequest();
//
//        // Validate NGO allowed transitions
//        validateNgoDonationProgress(offer.getStatus(), newStatus);
//
//        // Update donation progress only
//        offer.setStatus(newStatus);
//        donationOfferRepository.save(offer);
//
//        return "Donation progress updated successfully";
//    }

//    @Transactional
//    public String updateAssignedOfferStatus(Long ngoId,
//                                            Long assignmentId,
//                                            AssignmentStatus newStatus) {
//
//        ModeratorAssignmentEntity assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new RuntimeException("Assignment not found"));
//
//        if (!assignment.getReceiver().getId().equals(ngoId)) {
//            throw new RuntimeException("Not allowed");
//        }
//
//        if (!assignment.isActive()) {
//            throw new RuntimeException("Cannot update historical assignment");
//        }
//
//        DonationOfferEntity offer = assignment.getDonationRequest();
//
//        // --- NGO Reject ---
//        if (newStatus == AssignmentStatus.REJECTED_BY_RECEIVER) {
//
//            assignment.setStatus(AssignmentStatus.REJECTED_BY_RECEIVER);
//            assignmentRepository.save(assignment);
//
//            offer.setStatus(DonationOfferStatus.OPEN);
//            donationOfferRepository.save(offer);
//
//            return "Assignment rejected";
//        }
//
//        // --- Start Work ---
//        if (newStatus == AssignmentStatus.IN_PROGRESS &&
//                assignment.getStatus() == AssignmentStatus.ASSIGNED) {
//
//            assignment.setStatus(AssignmentStatus.IN_PROGRESS);
//            offer.setStatus(DonationOfferStatus.IN_PROGRESS);
//        }
//
//        // --- Mark Delivered ---
//        else if (newStatus == AssignmentStatus.COMPLETED &&
//                assignment.getStatus() == AssignmentStatus.IN_PROGRESS) {
//
//            assignment.setStatus(AssignmentStatus.COMPLETED);
//            offer.setStatus(DonationOfferStatus.DELIVERED);
//        }
//
//        else {
//            throw new RuntimeException("Invalid status transition");
//        }
//
//        assignmentRepository.save(assignment);
//        donationOfferRepository.save(offer);
//
//        return "Status updated successfully";
//    }
//
//    private void validateNgoDonationProgress(DonationOfferStatus oldStatus,
//                                             DonationOfferStatus newStatus) {
//
//        if (newStatus == DonationOfferStatus.IN_PROGRESS &&
//                oldStatus == DonationOfferStatus.ASSIGNED) return;
//
//        if (newStatus == DonationOfferStatus.COMPLETED &&
//                oldStatus == DonationOfferStatus.IN_PROGRESS) return;
//
//        throw new RuntimeException("Invalid status transition");
//    }

    @Transactional
    public String updateAssignedOfferStatus(Long ngoId,
                                            Long assignmentId,
                                            DonationOfferStatus newStatus) {

        ModeratorAssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getReceiver().getId().equals(ngoId)) {
            throw new RuntimeException("Not allowed");
        }

        if (!assignment.isActive()) {
            throw new RuntimeException("Cannot update historical assignment");
        }

        DonationOfferEntity offer = assignment.getDonationRequest();

        // --- NGO Reject ---
        if (newStatus == DonationOfferStatus.CANCELLED) {

            assignment.setStatus(AssignmentStatus.REJECTED_BY_RECEIVER);
            assignmentRepository.save(assignment);

            offer.setStatus(DonationOfferStatus.OPEN);
            donationOfferRepository.save(offer);

            return "Assignment rejected";
        }

        // --- Start Work ---
        if (newStatus == DonationOfferStatus.IN_PROGRESS &&
                offer.getStatus() == DonationOfferStatus.ASSIGNED) {

            offer.setStatus(DonationOfferStatus.IN_PROGRESS);
        }

        // --- Mark Delivered ---
        else if (newStatus == DonationOfferStatus.DELIVERED &&
                offer.getStatus() == DonationOfferStatus.IN_PROGRESS) {

            offer.setStatus(DonationOfferStatus.DELIVERED);
        }

        else {
            throw new RuntimeException("Invalid status transition");
        }

        donationOfferRepository.save(offer);

        return "Donation progress updated";
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

//    private void validateNgoVolunteerStatusChange(
//            AssignmentStatus oldStatus,
//            AssignmentStatus newStatus
//    ) {
//
//
//        if (newStatus == AssignmentStatus.COMPLETED &&
//                oldStatus == AssignmentStatus.ASSIGNED) return;
//
//        if (newStatus == AssignmentStatus.REJECTED_BY_RECEIVER &&
//                (oldStatus == AssignmentStatus.ASSIGNED || oldStatus == AssignmentStatus.IN_PROGRESS)) return;
//
//        throw new RuntimeException("Invalid volunteer status transition");
//    }
private void validateNgoVolunteerStatusChange(
        AssignmentStatus oldStatus,
        AssignmentStatus newStatus
) {

    // Start work
    if (newStatus == AssignmentStatus.IN_PROGRESS &&
            oldStatus == AssignmentStatus.ASSIGNED) return;

    // Mark delivered
    if (newStatus == AssignmentStatus.COMPLETED &&
            oldStatus == AssignmentStatus.IN_PROGRESS) return;

    // Reject
    if (newStatus == AssignmentStatus.REJECTED_BY_RECEIVER &&
            (oldStatus == AssignmentStatus.ASSIGNED||oldStatus == AssignmentStatus.IN_PROGRESS)) return;

    throw new RuntimeException("Invalid volunteer status transition");
}

//    @Transactional
//    public String updateAssignedVolunteerStatus(
//            Long ngoId,
//            Long assignmentId,
//            AssignmentStatus newStatus
//    ) {
//
//        VolunteerAssignmentEntity assignment =
//                volunteerAssignmentRepository.findById(assignmentId)
//                        .orElseThrow(() -> new RuntimeException("Volunteer assignment not found"));
//
//        // 🔐 Security check
//        if (!assignment.getReceiver().getId().equals(ngoId)) {
//            throw new RuntimeException("Not allowed");
//        }
//
//        if (!assignment.isActive()) {
//            throw new RuntimeException("Cannot update historical assignment");
//        }
//
//        // Validate transition
//        validateNgoVolunteerStatusChange(assignment.getStatus(), newStatus);
//
//        // Update assignment
//        assignment.setStatus(newStatus);
//        volunteerAssignmentRepository.save(assignment);
//
//        // Update volunteer request status
//        VolunteerRequestEntity request = assignment.getVolunteerRequest();
//        request.setStatus(mapToVolunteerOfferStatus(newStatus));
//
//        return "Volunteer status updated successfully";
//    }

    @Transactional
    public String updateAssignedVolunteerStatus(
            Long ngoId,
            Long assignmentId,
            AssignmentStatus newStatus
    ) {

        VolunteerAssignmentEntity assignment =
                volunteerAssignmentRepository.findById(assignmentId)
                        .orElseThrow(() -> new RuntimeException("Volunteer assignment not found"));

        if (!assignment.getReceiver().getId().equals(ngoId)) {
            throw new RuntimeException("Not allowed");
        }

        VolunteerRequestEntity request = assignment.getVolunteerRequest();

        validateNgoVolunteerStatusChange(
                assignment.getStatus(),
                newStatus
        );

        // START WORK
        if (newStatus == AssignmentStatus.IN_PROGRESS) {
            assignment.setStatus(AssignmentStatus.IN_PROGRESS);
            request.setStatus(VolunteerOfferStatus.IN_PROGRESS);
        }

        // MARK DELIVERED
        else if (newStatus == AssignmentStatus.COMPLETED) {
            assignment.setStatus(AssignmentStatus.COMPLETED);
            request.setStatus(VolunteerOfferStatus.DELIVERED);
        }

        // REJECT
        else if (newStatus == AssignmentStatus.REJECTED_BY_RECEIVER) {
            assignment.setStatus(AssignmentStatus.REJECTED_BY_RECEIVER);
            request.setStatus(VolunteerOfferStatus.OPEN);
        }

        volunteerAssignmentRepository.save(assignment);
        volunteerRequestRepository.save(request);

        return "Volunteer status updated successfully";
    }

    private VolunteerOfferStatus mapToVolunteerOfferStatus(AssignmentStatus status) {

        switch (status) {

            case COMPLETED:
                return VolunteerOfferStatus.COMPLETED;

            case REJECTED_BY_RECEIVER:
                return VolunteerOfferStatus.OPEN;

            default:
                return VolunteerOfferStatus.ASSIGNED;
        }
    }

}
