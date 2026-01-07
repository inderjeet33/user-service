package com.prerana.userservice.service;

import com.prerana.userservice.dto.DonationOfferDto;
import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.dto.NgoProfile;
import com.prerana.userservice.dto.NgoPublicDto;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.mapper.DonationOfferDtoMapper;
import com.prerana.userservice.mapper.UserEntityMapper;
import com.prerana.userservice.repository.DonationOfferRepository;
import com.prerana.userservice.repository.ModeratorAssignmentRepository;
import com.prerana.userservice.repository.NGOProfileRepository;
import com.prerana.userservice.repository.UserRepository;
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

    public DonationOffersRequestDto createDonationOffer(Long userId, DonationOffersRequestDto dto) {

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
                .build();

        return mapperUtil.toDto(donationOfferRepository.save(offer));
    }

    @Transactional
    public void cancelDonationOffer(Long offerId, Long userId) {
        DonationOfferEntity offer = donationOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Donation offer not found"));

        // Ownership check
        if (!offer.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not allowed to cancel this donation");
        }

        // Only allow cancel for specific states
        if (!(offer.getStatus() == DonationOfferStatus.OPEN ||
                offer.getStatus() == DonationOfferStatus.ASSIGNED)) {
            throw new RuntimeException("Donation cannot be cancelled in this state");
        }

        // Update donation offer
        offer.setStatus(DonationOfferStatus.CANCELLED);
        donationOfferRepository.save(offer);

        // If assignment exists → cancel assignment
        ModeratorAssignmentEntity assignmentEntity = repository.findByDonationRequest_IdAndStatusIn(offerId,List.of(AssignmentStatus.ASSIGNED,AssignmentStatus.IN_PROGRESS));
        if(assignmentEntity!=null) {
            assignmentEntity.setStatus(AssignmentStatus.CANCELLED_BY_DONOR);
            repository.save(assignmentEntity);
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
                        List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.IN_PROGRESS,AssignmentStatus.COMPLETED)
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
                .email(ngoOwner.getEmail())
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
    public Page<DonationOffersRequestDto> search(
            int page,
            int size,
            String search,
            String category,
            String type,
            DonationOfferStatus status
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<DonationOfferEntity> pageEntities =
                donationOfferRepository.search(
                        normalize(search),
                        normalize(category),
                        normalize(type),
                        status,
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

    public List<DonationOffersRequestDto> getOffersByUser(Long userId) {
        List<DonationOffersRequestDto> dtos = mapperUtil.toDtoList(donationOfferRepository.findByUserId(userId));
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

    private void populateReceiverDetailsInDto(Page<DonationOffersRequestDto> dtos) {

        for (DonationOffersRequestDto dto : dtos) {

            Optional<ModeratorAssignmentEntity> optionalAssignment =
                    repository.findTopByDonationRequest_IdOrderByCreatedAtDesc(dto.getId());

            if (optionalAssignment.isEmpty()) continue;

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
        ModeratorAssignmentEntity entity = repository.findByDonationRequest_IdAndStatusIn(dto.getId(),List.of(AssignmentStatus.ASSIGNED,AssignmentStatus.IN_PROGRESS,AssignmentStatus.COMPLETED));
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
}
