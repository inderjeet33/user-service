package com.prerana.userservice.service;

import com.prerana.userservice.certificate.CertificatePdfGenerator;
import com.prerana.userservice.dto.DonationOfferDto;
import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.dto.NgoProfile;
import com.prerana.userservice.dto.NgoPublicDto;
import com.prerana.userservice.entity.*;
import com.prerana.userservice.enums.*;
import com.prerana.userservice.mapper.DonationOfferDtoMapper;
import com.prerana.userservice.mapper.UserEntityMapper;
import com.prerana.userservice.repository.*;
import com.prerana.userservice.service.interfaces.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonationOfferService {

    @Autowired
    private UserService userService;

    @Autowired
    private NGOProfileService ngoProfileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationOfferDtoMapper mapperUtil;

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Autowired
    private ModeratorAssignmentRepository repository;

    @Autowired
    private final DonationOfferRepository donationOfferRepository;
    @Autowired
    private NGOProfileRepository nGOProfileRepository;

    @Autowired
    private ModeratorAssignmentRepository assignmentRepository;

    @Autowired
    private CertificateRepository certificateRepo;


    @Autowired
    private SubscriptionGuardService subscriptionGuardService;

    public DonationOffersRequestDto createDonationOffer(Long userId, DonationOffersRequestDto dto) {

//        subscriptionGuardService.checkDonationOfferCreation(userId);
        Optional<UserEntity> user = userRepository.findById(userId);
        DonationOfferEntity offer = DonationOfferEntity.builder()
                .user(user.get())
                .amount(dto.getAmount())
                .donationCategory(dto.getDonationCategory())
                .timeLine(dto.getTimeLine())
                .recurringHelp(dto.isRecurringHelp())
                .reason(dto.getReason())
                .type(dto.getType())
                .ageGroup(dto.getAgeGroup())
                .gender(dto.getGender())
                .location(dto.getLocation())
                .status(DonationOfferStatus.OPEN)
                .preferredContact(dto.getPreferredContact())
                .helpType(dto.getHelpType())
                .itemDetails(dto.getItemDetails())
                .quantity(dto.getQuantity())
                .build();

        return mapperUtil.toDto(donationOfferRepository.save(offer));
    }

//    @Transactional
//    public void cancelDonationOffer(Long offerId, Long userId) {
//        DonationOfferEntity offer = donationOfferRepository.findById(offerId)
//                .orElseThrow(() -> new RuntimeException("Donation offer not found"));
//
//        // Ownership check
//        if (!offer.getUser().getId().equals(userId)) {
//            throw new RuntimeException("Not allowed to cancel this donation");
//        }
//
//        // Only allow cancel for specific states
//        if (!(offer.getStatus() == DonationOfferStatus.OPEN ||
//                offer.getStatus() == DonationOfferStatus.ASSIGNED)) {
//            throw new RuntimeException("Donation cannot be cancelled in this state");
//        }
//
//        // Update donation offer
//        offer.setStatus(DonationOfferStatus.CANCELLED);
//        donationOfferRepository.save(offer);
//
//        // If assignment exists → cancel assignment
//        ModeratorAssignmentEntity assignmentEntity = repository.findByDonationRequest_IdAndStatusIn(offerId,List.of(AssignmentStatus.ASSIGNED));
//        if(assignmentEntity!=null) {
//            assignmentEntity.setStatus(AssignmentStatus.CANCELLED_BY_DONOR);
//            repository.save(assignmentEntity);
//        }
//    }

    @Transactional
    public void cancelDonationOffer(Long offerId, Long userId) {

        DonationOfferEntity offer = donationOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Donation offer not found"));

        // Ownership check
        if (!offer.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not allowed to cancel this donation");
        }

        // Do not allow cancel after completion
        if (offer.getStatus() == DonationOfferStatus.COMPLETED) {
            throw new RuntimeException("Completed donations cannot be cancelled");
        }

        if (offer.getStatus() == DonationOfferStatus.CANCELLED ||
                offer.getStatus() == DonationOfferStatus.EXPIRED) {
            throw new RuntimeException("Donation already closed");
        }

        // Update donation offer status
        offer.setStatus(DonationOfferStatus.CANCELLED);
        donationOfferRepository.save(offer);

        // Cancel active assignment (if exists)
        Optional<ModeratorAssignmentEntity> optionalAssignment =
                assignmentRepository.findTopByDonationRequest_IdAndStatusInOrderByCreatedAtDesc(
                        offerId,
                        List.of(AssignmentStatus.ASSIGNED)
                );

        if (optionalAssignment.isPresent()) {
            ModeratorAssignmentEntity assignment = optionalAssignment.get();
            assignment.setStatus(AssignmentStatus.CANCELLED_BY_DONOR);
            assignmentRepository.save(assignment);
        }
    }

    public NgoPublicDto getReceiverForDonation(Long donationId, Long userId) {

        DonationOfferEntity offer = donationOfferRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        // donor-only access
        if (!offer.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not allowed");
        }

        if (!(offer.getStatus() == DonationOfferStatus.ASSIGNED
                || offer.getStatus() == DonationOfferStatus.IN_PROGRESS
                || offer.getStatus() == DonationOfferStatus.COMPLETED)) {
            throw new RuntimeException("NGO not yet assigned");
        }

        ModeratorAssignmentEntity assignment =
                repository.findTopByDonationRequest_IdAndStatusInOrderByCreatedAtDesc(
                        donationId,
                        List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.COMPLETED)
                ).orElseThrow(() -> new RuntimeException("No active assignment"));

        UserEntity ngoOwner = assignment.getReceiver();
        Optional<NGOProfileEntity> ngoOptional = nGOProfileRepository.findByUserId(ngoOwner.getId());
        if(ngoOptional.isEmpty()){
            throw new RuntimeException("Ngo Profile is not present");
        }
        NGOProfileEntity ngo = ngoOptional.get();
        return NgoPublicDto.builder()
                .id(ngo.getId())
                .name(ngo.getNgoName())
                .email(ngo.getEmail())
                .mobile(ngoOwner.getMobileNumber())
                .address(ngo.getCity()+":"+ngo.getState())
                .city(ngo.getCity())
                .state(ngo.getState())
                .description(ngo.getDescription())
                .ownerId(ngoOwner.getId())
                .website("https://goodwillngo.com")
                .build();
    }

    public Page<DonationOffersRequestDto> search(int page,int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<DonationOfferEntity> pageEntities = donationOfferRepository.findAll(pageable);
        Page<DonationOffersRequestDto> pageDtos = mapperUtil.toDtoPage(pageEntities);
        populateReceiverDetailsInDto(pageDtos);
        return pageDtos;
    }
//    public Page<DonationOffersRequestDto> search(
//            int page,
//            int size,
//            String search,
//            String category,
//            String type,
//            DonationOfferStatus status
//    ) {
//        Pageable pageable = PageRequest.of(
//                page,
//                size,
//                Sort.by("createdAt").descending()
//        );
//
//        Page<DonationOfferEntity> pageEntities =
//                donationOfferRepository.search(
//                        normalize(search),
//                        StringUtils.isBlank(category) ? null : DonationCategory.valueOf(normalize(category)),
//                        StringUtils.isBlank(type) ? null : HelpType.valueOf(normalize(type)),
//                        status,
//                        pageable
//                );
//
//        Page<DonationOffersRequestDto> pageDtos =
//                mapperUtil.toDtoPage(pageEntities);
//
//        populateReceiverDetailsInDto(pageDtos);
//
//        return pageDtos;
//    }

    public Page<DonationOffersRequestDto> search(
            int page,
            int size,
            String search,
            String category,
            String type,
            DonationOfferStatus status,
            String view,
            String sortParam
    ) {

        // -------- SORTING --------
        String[] sortParts = sortParam.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction =
                sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

        // -------- VIEW FILTER LOGIC --------
        List<DonationOfferStatus> viewStatuses = null;

        if (status == null) {
            if ("ACTIVE".equalsIgnoreCase(view)) {
                viewStatuses = List.of(
                        DonationOfferStatus.OPEN,
                        DonationOfferStatus.ASSIGNED,
                        DonationOfferStatus.IN_PROGRESS,
                        DonationOfferStatus.UNDER_REVIEW
                );
            } else if ("HISTORY".equalsIgnoreCase(view)) {
                viewStatuses = List.of(
                        DonationOfferStatus.COMPLETED,
                        DonationOfferStatus.EXPIRED,
                        DonationOfferStatus.CANCELLED,
                        DonationOfferStatus.DELIVERED
                );
            }
        }

        Page<DonationOfferEntity> pageEntities =
                donationOfferRepository.searchWithView(
                        normalizeForJPQL(search),
                        StringUtils.isBlank(category) ? null : DonationCategory.valueOf(normalize(category)),
                        StringUtils.isBlank(type) ? null : HelpType.valueOf(normalize(type)),
                        status,
                        viewStatuses,
                        pageable
                );

        Page<DonationOffersRequestDto> pageDtos =
                mapperUtil.toDtoPage(pageEntities);

        populateReceiverDetailsInDto(pageDtos);

        return pageDtos;
    }
    /* small helper */
    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private String normalizeForJPQL(String search){
        String searchPattern = null;

        if (StringUtils.isNotBlank(search)) {
            searchPattern = "%" + search.trim().toLowerCase() + "%";
        }
        return searchPattern;
    }

//    public List<DonationOffersRequestDto> getOffersByUser(Long userId) {
//        List<DonationOffersRequestDto> dtos = mapperUtil.toDtoList(donationOfferRepository.findByUserId(userId));
//        dtos.forEach(this::populateReceiverDetails);
//        return dtos;
//    }

    public List<DonationOffersRequestDto> getOffersByUser(Long userId, String view) {

        List<DonationOfferStatus> statuses;

        if ("HISTORY".equalsIgnoreCase(view)) {
            statuses = List.of(
                    DonationOfferStatus.COMPLETED,
                    DonationOfferStatus.CANCELLED,
                    DonationOfferStatus.EXPIRED
            );
        } else { // ACTIVE default
            statuses = List.of(
                    DonationOfferStatus.OPEN,
                    DonationOfferStatus.ASSIGNED,
                    DonationOfferStatus.IN_PROGRESS,
                    DonationOfferStatus.DELIVERED,
                    DonationOfferStatus.UNDER_REVIEW
            );
        }

        List<DonationOfferEntity> entities =
                donationOfferRepository.findByUser_IdAndStatusInOrderByCreatedAtDesc(
                        userId, statuses
                );

        List<DonationOffersRequestDto> dtos = mapperUtil.toDtoList(entities);
        dtos.forEach(this::populateReceiverDetails);

        return dtos;
    }
    public DonationOfferEntity getOfferById(Long id, Long userId) {
        DonationOfferEntity offer = donationOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        return offer;
    }

//    private void populateReceiverDetailsInDto(Page<DonationOffersRequestDto> dtos) {
//
//        for (DonationOffersRequestDto dto : dtos) {
//
//            Optional<ModeratorAssignmentEntity> optionalAssignment =
//                    repository.findTopByDonationRequest_IdOrderByCreatedAtDesc(dto.getId());
//
//            if (optionalAssignment.isEmpty()) continue;
//
//            ModeratorAssignmentEntity entity = optionalAssignment.get();
//
//            dto.setAssignmentStatus(entity.getStatus());
//            dto.setReceiverType(entity.getReceiver().getUserType().name());
//            dto.setReceiverId(entity.getReceiver().getId());
//            dto.setReceiverMobile(entity.getReceiver().getMobileNumber());
//            dto.setReceiverEmail(entity.getReceiver().getEmail());
//
//            if (entity.getReceiver().getUserType() == UserType.NGO) {
//                NgoProfile ngoProfile = ngoProfileService
//                        .getProfileByUserId(dto.getReceiverId())
//                        .orElseThrow(() ->
//                                new RuntimeException("NGO profile missing for userId " + dto.getReceiverId())
//                        );
//
//                dto.setReceiverName(ngoProfile.getNgoName());
//                dto.setReceiverCity(ngoProfile.getCity());
//            } else {
//                dto.setReceiverName(entity.getReceiver().getFullName());
//            }
//        }
//    }

    @Transactional
    public String confirmDelivery(Long offerId, Long userId) {

        DonationOfferEntity offer = donationOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (offer.getStatus() != DonationOfferStatus.DELIVERED) {
            throw new RuntimeException("Not ready for confirmation");
        }

        offer.setStatus(DonationOfferStatus.COMPLETED);
        donationOfferRepository.save(offer);

        // close moderator assignment
        ModeratorAssignmentEntity assignment =
                assignmentRepository.findTopByDonationRequest_IdAndStatusInOrderByCreatedAtDesc(
                        offerId,
                        List.of(AssignmentStatus.ASSIGNED)
                ).orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignmentRepository.save(assignment);

        completeAssignment(offer.getAmount(), assignment);

        return "Donation completed successfully";
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

    private void populateReceiverDetailsInDto(Page<DonationOffersRequestDto> dtos) {

        for (DonationOffersRequestDto dto : dtos) {

            Optional<ModeratorAssignmentEntity> optionalAssignment =
                    repository.findTopByDonationRequest_IdAndStatusInOrderByCreatedAtDesc(
                            dto.getId(),
                            List.of(
                                    AssignmentStatus.ASSIGNED,
                                    AssignmentStatus.COMPLETED
                            )
                    );

            if (optionalAssignment.isEmpty()) {
                dto.setReceiverName(null);
                dto.setReceiverId(null);
                dto.setReceiverEmail(null);
                dto.setReceiverMobile(null);
                continue;
            }

            ModeratorAssignmentEntity entity = optionalAssignment.get();

            dto.setAssignmentStatus(entity.getStatus());
            dto.setReceiverType(entity.getReceiver().getUserType().name());
            dto.setReceiverId(entity.getReceiver().getId());
            dto.setReceiverMobile(entity.getReceiver().getMobileNumber());
            dto.setReceiverEmail(entity.getReceiver().getEmail());

            if (entity.getReceiver().getUserType() == UserType.NGO) {
                NgoProfile ngoProfile = ngoProfileService
                        .getProfileByUserId(dto.getReceiverId())
                        .orElseThrow(() ->
                                new RuntimeException("NGO profile missing for userId " + dto.getReceiverId())
                        );

                dto.setReceiverName(ngoProfile.getNgoName());
                dto.setReceiverCity(ngoProfile.getCity());
            } else {
                dto.setReceiverName(entity.getReceiver().getFullName());
            }
        }
    }


//    private void populateReceiverDetailsInDto(Page<DonationOffersRequestDto> dtos){
//        for(DonationOffersRequestDto dto : dtos){
//            // fetch one by one for now
//            ModeratorAssignmentEntity entity = repository.findByDonationRequest_IdAndStatusIn(dto.getId(),List.of(AssignmentStatus.ASSIGNED,AssignmentStatus.IN_PROGRESS));
////            ModeratorAssignmentEntity entity = repository.findByDonationRequest_IdAndStatusIn(dto.getId(),status);
//            if(Objects.nonNull(entity)){
//                dto.setReceiverType(entity.getReceiver().getUserType().name());
//                dto.setReceiverId(entity.getReceiver().getId());
//                dto.setReceiverMobile(entity.getReceiver().getMobileNumber());
//                dto.setReceiverEmail(entity.getReceiver().getEmail());
//                if(dto.getReceiverType().equals(UserType.NGO.name())){
//                    Optional<NgoProfile> ngoProfile = ngoProfileService.getProfileByUserId(dto.getReceiverId());
//                    if(ngoProfile.isEmpty()){
//                        throw new RuntimeException(String.format("Ngo profile missing, User Id %s ",dto.getReceiverId()));
//                    }
//                    dto.setReceiverName(ngoProfile.get().getNgoName());
//                    dto.setReceiverCity(ngoProfile.get().getCity());
//                }
//                else{
//                    dto.setReceiverName(entity.getReceiver().getFullName());
//                }
//            }
////            dto.setReceiver(Objects.nonNull(entity) ? userEntityMapper.toDto(entity.getReceiver()) : null);
//        }
//    }
    private void populateReceiverDetails(DonationOffersRequestDto dto){
        ModeratorAssignmentEntity entity = repository.findByDonationRequest_IdAndStatusIn(dto.getId(),List.of(AssignmentStatus.ASSIGNED,AssignmentStatus.COMPLETED));
//            ModeratorAssignmentEntity entity = repository.findByDonationRequest_IdAndStatusIn(dto.getId(),status);
        if(Objects.nonNull(entity)){
            dto.setReceiverType(entity.getReceiver().getUserType().name());
            dto.setReceiverId(entity.getReceiver().getId());
            dto.setReceiverMobile(entity.getReceiver().getMobileNumber());
            dto.setReceiverEmail(entity.getReceiver().getEmail());
            if(dto.getReceiverType().equals(UserType.NGO.name())){
                Optional<NgoProfile> ngoProfile = ngoProfileService.getProfileByUserId(dto.getReceiverId());
                if(ngoProfile.isEmpty()){
                    throw new RuntimeException(String.format("Ngo profile missing, User Id %s ",dto.getReceiverId()));
                }
                dto.setReceiverName(ngoProfile.get().getNgoName());
                dto.setReceiverCity(ngoProfile.get().getCity());
            }
            else{
                dto.setReceiverName(entity.getReceiver().getFullName());
            }
        }
//            dto.setReceiver(Objects.nonNull(entity) ? userEntityMapper.toDto(entity.getReceiver()) : null);
    }

    @Transactional
    public String updateAssignedOfferStatus(Long ngoId,
                                            Long assignmentId,
                                            AssignmentStatus newStatus) {

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
        if (newStatus == AssignmentStatus.REJECTED_BY_RECEIVER) {

            assignment.setStatus(AssignmentStatus.REJECTED_BY_RECEIVER);
            assignmentRepository.save(assignment);

            offer.setStatus(DonationOfferStatus.OPEN);
            donationOfferRepository.save(offer);

            return "Assignment rejected";
        }

        // --- Start Work ---
        if (newStatus == AssignmentStatus.IN_PROGRESS &&
                assignment.getStatus() == AssignmentStatus.ASSIGNED) {

            assignment.setStatus(AssignmentStatus.IN_PROGRESS);
            offer.setStatus(DonationOfferStatus.IN_PROGRESS);
        }

        // --- Mark Delivered ---
        else if (newStatus == AssignmentStatus.COMPLETED &&
                assignment.getStatus() == AssignmentStatus.IN_PROGRESS) {

            assignment.setStatus(AssignmentStatus.COMPLETED);
            offer.setStatus(DonationOfferStatus.DELIVERED);
        }

        else {
            throw new RuntimeException("Invalid status transition");
        }

        assignmentRepository.save(assignment);
        donationOfferRepository.save(offer);

        return "Status updated successfully";
    }
}
