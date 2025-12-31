package com.prerana.userservice.service;

import com.prerana.userservice.dto.CampaignPublicDto;
import com.prerana.userservice.dto.CampaignResponseDto;
import com.prerana.userservice.dto.CreateCampaignDto;
import com.prerana.userservice.entity.CampaignEntity;
import com.prerana.userservice.entity.GalleryImageEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.CampaignStatus;
import com.prerana.userservice.enums.GalleryStatus;
import com.prerana.userservice.enums.OwnerType;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.mapper.CampaignDtoMappper;
import com.prerana.userservice.repository.CampaignRepository;
import com.prerana.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    @Autowired
    private CampaignDtoMappper campaignDtoMappper;

    @Transactional
    public CampaignResponseDto createCampaign(CreateCampaignDto dto, Long ownerId, MultipartFile image) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

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
                .build();


        campaignRepository.save(campaign);

        return campaignDtoMappper.toDto(campaign);
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

    public CampaignResponseDto getCampaignById(Long id) {
        CampaignEntity c = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        CampaignResponseDto responseDto = campaignDtoMappper.toDto(c);
        responseDto.setOwnerId(c.getOwner().getId());
        responseDto.setOwnerType(c.getOwnerType());
        responseDto.setOwnerName(c.getOwner().getFullName());
        responseDto.setMobileNumber(c.getOwner().getMobileNumber());
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