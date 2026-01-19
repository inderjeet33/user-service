package com.prerana.userservice.service;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.*;
import com.prerana.userservice.enums.*;
import com.prerana.userservice.exceptions.InvalidRaisedAmountException;
import com.prerana.userservice.exceptions.NgoProfileMissingException;
import com.prerana.userservice.mapper.CampaignDtoMappper;
import com.prerana.userservice.repository.CampaignRepository;
import com.prerana.userservice.repository.CampaignUpdateRepository;
import com.prerana.userservice.repository.NGOProfileRepository;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

    @Autowired
    private final CampaignRepository campaignRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private CampaignDtoMappper campaignDtoMappper;

    @Autowired
    private CampaignUpdateRepository updateCampaignRepository;

    @Autowired
    private NGOProfileRepository profileRepository;

    @Transactional
    public CampaignResponseDto createCampaign(CreateCampaignDto dto, Long ownerId, MultipartFile image) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        checkNgoVerified(owner);
        String imageUrl= "";
        try {
             imageUrl = saveImage(ownerId, image);
        }catch(IOException e){
            log.error("Got io exception");
        }
        CampaignEntity campaign = CampaignEntity.builder()
                .owner(owner)
                .ownerType(OwnerType.NGO)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .status(CampaignStatus.ACTIVE)
                .targetAmount(dto.getTargetAmount())
                .deadline(dto.getDeadline())
                .mediaUrls(dto.getMediaUrls())
                .city(dto.getCity())
                .state(dto.getState())
                .address(dto.getLocation())
                .imageUrl(imageUrl)
                .beneficiaryType(dto.getBeneficiaryType())
                .beneficiaryCount(dto.getBeneficiaryCount())
                .raisedAmount(dto.getRaisedAmount() != null ? dto.getRaisedAmount() : 0.0)
                .build();


        campaignRepository.save(campaign);

        return campaignDtoMappper.toDto(campaign);
    }

    private void checkNgoVerified(UserEntity owner){
        Boolean verifiedProfileExists = false;
        if(Objects.nonNull(owner)){
            Optional<NGOProfileEntity> ngoProfileEntityOptional = profileRepository.findByUser_id(owner.getId());
            if(ngoProfileEntityOptional.isPresent()){
                NGOProfileEntity entity = ngoProfileEntityOptional.get();
                if(ActivationStatus.VERIFIED == entity.getActivationStatus()){
                    verifiedProfileExists = true;
                }
            }
        }
        if(!verifiedProfileExists){
            throw new NgoProfileMissingException("Please create a verified profile for your NGO");
        }
    }
    public CampaignResponseDto updateCampaign(Long id, Long ownerId, UpdateCampaignDto dto) {

        CampaignEntity campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!campaign.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Not authorized");
        }

        campaign.setTitle(dto.getTitle());
        campaign.setDescription(dto.getDescription());
        campaign.setCategory(dto.getCategory());
        campaign.setTargetAmount(dto.getTargetAmount());
        campaign.setDeadline(dto.getDeadline());
        campaign.setUrgency(dto.getUrgency());
        campaign.setCity(dto.getCity());
        campaign.setState(dto.getState());
        campaign.setAddress(dto.getAddress());
        if(Objects.nonNull(dto.getRaisedAmount())){
            if(dto.getRaisedAmount() > dto.getTargetAmount()){
                throw new InvalidRaisedAmountException("Raised amount can not be more than target exception");
            }
        }
        campaign.setRaisedAmount(dto.getRaisedAmount());

        campaignRepository.save(campaign);

        CampaignResponseDto responseDto = campaignDtoMappper.toDto(campaign);
        responseDto.setOwnerId(campaign.getOwner().getId());
        responseDto.setOwnerType(campaign.getOwnerType());
        responseDto.setOwnerName(campaign.getOwner().getFullName());
        responseDto.setMobileNumber(campaign.getOwner().getMobileNumber());
        responseDto.setBeneficiaryCount(campaign.getBeneficiaryCount());
        responseDto.setBeneficiaryType(campaign.getBeneficiaryType());

        return responseDto;
    }

    public void markAsCompleted(Long id, Long ownerId) {

        CampaignEntity campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!campaign.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Not authorized");
        }

        campaign.setStatus(CampaignStatus.COMPLETED);
        campaignRepository.save(campaign);
    }

    private String saveImage(Long ownerId,MultipartFile file)throws IOException {

        Path uploadDir = Paths.get("uploads/campaigns");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/campaigns/"+fileName;
//        Optional<UserEntity> user = userRepo.findById(ngoId);
//        GalleryImageEntity entityImage = new GalleryImageEntity();
//        entityImage.setNgo(user.get());
//        entityImage.setImagePath("/uploads/ngo-gallery/" + fileName);
//        entityImage.setCaption(caption);
//        entityImage.setStatus(GalleryStatus.PENDING);
//
//        galleryRepo.save(entityImage);
    }

    public List<CampaignResponseDto> getAllActiveCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.ACTIVE)
                .stream()
                .map(campaignDtoMappper::toDto)
                .collect(Collectors.toList());
    }

    public List<CampaignPublicDto> getPublicCampaigns() {

        return campaignRepository.findByStatus(CampaignStatus.ACTIVE)
                .stream()
                .map(c -> {
                    CampaignPublicDto dto = new CampaignPublicDto();
                    dto.setId(c.getId());
                    dto.setTitle(c.getTitle());
                    dto.setDescription(c.getDescription());
                    dto.setImageUrl(c.getImageUrl());
                    dto.setTargetAmount(c.getTargetAmount());
                    dto.setNgoName(c.getOwner().getFullName());
                    dto.setRaisedAmount(c.getRaisedAmount());
                    return dto;
                })
                .toList();
    }


    public List<CampaignResponseDto> getCampaignsByOwner(Long ownerId) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        return campaignRepository.findByOwner(owner)
                .stream()
                .map(campaignDtoMappper::toDto)
                .collect(Collectors.toList());
    }

    public List<CampaignResponseDto> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status)
                .stream()
                .map(campaignDtoMappper::toDto).map(this::populateOtherDetails)
                .toList();
    }

    private CampaignResponseDto populateOtherDetails(CampaignResponseDto responseDto){
        Optional<CampaignEntity> entity = campaignRepository.findById(responseDto.getId());
        if(entity.isPresent()){
            CampaignEntity ent = entity.get();
            responseDto.setOwnerName(ent.getOwner().getFullName());
            Optional<NGOProfileEntity> profileEntityOptional = profileRepository.findByUser_id(ent.getOwner().getId());
            profileEntityOptional.ifPresent(ngoProfileEntity -> responseDto.setOwnerName(ngoProfileEntity.getNgoName()));
            responseDto.setMobileNumber( ent.getOwner().getMobileNumber());
        }
        return responseDto;
    }

    @Transactional
    public void approveCampaign(Long id) {
        CampaignEntity c = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        c.setStatus(CampaignStatus.ACTIVE);
        c.setRejectionReason(null);

        campaignRepository.save(c);
    }

    @Transactional
    public void rejectCampaign(Long id, String reason) {
        CampaignEntity c = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        c.setStatus(CampaignStatus.REJECTED);
        c.setRejectionReason(reason);

        campaignRepository.save(c);
    }


    @Transactional
    public void withdrawCampaign(Long campaignId, Long userId) {

        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!campaign.getOwner().getId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this campaign");
        }

        if (campaign.getStatus() == CampaignStatus.COMPLETED ||
                campaign.getStatus() == CampaignStatus.WITHDRAWN) {

            throw new RuntimeException("Campaign is already closed");
        }

        campaign.setStatus(CampaignStatus.WITHDRAWN);
        campaignRepository.save(campaign);
    }
    @Transactional
    public void addUpdate(Long campaignId, Long ownerId, CreateCampaignUpdateDto dto) {

        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!campaign.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

        CampaignUpdateEntity update = CampaignUpdateEntity.builder()
                .campaign(campaign)
                .message(dto.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        updateCampaignRepository.save(update);
    }

    public List<CampaignUpdateResponseDto> getUpdates(Long campaignId) {
        return updateCampaignRepository.findByCampaignIdOrderByCreatedAtDesc(campaignId)
                .stream()
                .map(u -> new CampaignUpdateResponseDto(u.getMessage(), u.getCreatedAt()))
                .toList();
    }



    public CampaignResponseDto getCampaignById(Long id) {
        CampaignEntity c = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        CampaignResponseDto responseDto = campaignDtoMappper.toDto(c);
        responseDto.setOwnerId(c.getOwner().getId());
        responseDto.setOwnerType(c.getOwnerType());
        responseDto.setOwnerName(c.getOwner().getFullName());
        responseDto.setMobileNumber(c.getOwner().getMobileNumber());
        responseDto.setBeneficiaryCount(c.getBeneficiaryCount());
        responseDto.setBeneficiaryType(c.getBeneficiaryType());

        return responseDto;
    }
//    CampaignResponseDto.builder()
//            .id(entity.getId())
//            .title(entity.getTitle())
//            .description(entity.getDescription())
//            .category(entity.getCategory())
//            .targetAmount(entity.getTargetAmount())
//            .deadLine(entity.getDeadline())
//            .location(entity.getLocation())
//            .status(entity.getStatus())
//            .mediaUrls(entity.getMediaUrls())
//            .ownerId(entity.getOwner().getId())
//            .ownerName(entity.getOwner().getFullName())
//            .ownerType(entity.getOwnerType())
//            .build();
//}
}