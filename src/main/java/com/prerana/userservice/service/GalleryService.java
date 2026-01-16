package com.prerana.userservice.service;

import com.prerana.userservice.dto.GalleryImageDto;
import com.prerana.userservice.entity.GalleryImageEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.GalleryStatus;
import com.prerana.userservice.exceptions.NgoProfileMissingException;
import com.prerana.userservice.repository.GalleryImageRepository;
import com.prerana.userservice.repository.NGOProfileRepository;
import com.prerana.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GalleryService {

    @Autowired
    private final GalleryImageRepository galleryRepo;

    @Autowired
    private final UserRepository userRepo;

    @Autowired
    private NGOProfileRepository ngoProfileRepository;

    // return approved/public images of the ngo
    public List<GalleryImageDto> getApprovedImagesForNgo(Long userId,Long ngoId) {
        //for now returning all images
        //validate that user is allowed to see this ngos images
        return galleryRepo.findByNgo_Id(
                        ngoId
                ).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void approveImage(Long imageId)
    {
        Optional<GalleryImageEntity> entityOptional = galleryRepo.findById(imageId);
        if(entityOptional.isPresent()){
            GalleryImageEntity entity = entityOptional.get();
            entity.setStatus(GalleryStatus.APPROVED);
            entity.setRejectionReason(null);
            galleryRepo.save(entity);
        }
    }

    @Transactional
    public void rejectImage(Long imageId, Map<String,String> body){
        Optional<GalleryImageEntity> entityOptional = galleryRepo.findById(imageId);
        if(entityOptional.isPresent()){
            GalleryImageEntity galleryImageEntity = entityOptional.get();
            galleryImageEntity.setRejectionReason(Objects.nonNull(body)? body.getOrDefault("reason","Image was not approved") : "Image was not approved");
            galleryImageEntity.setStatus(GalleryStatus.REJECTED);
            galleryRepo.save(galleryImageEntity);
        }
    }
    public List<GalleryImageDto> findByStatusAndNgoId(Long ngoId,GalleryStatus gallerystatus)
    {
        Optional<NGOProfileEntity> ngoProfileEntity = ngoProfileRepository.findByUserId(ngoId);
        String name;
        if (ngoProfileEntity.isEmpty()) {
            throw new NgoProfileMissingException();
        } else {
            name = ngoProfileEntity.get().getNgoName();
        }

        List<GalleryImageEntity> galleryImageEntities = galleryRepo.findByNgo_IdAndStatus(ngoId,gallerystatus);
        return galleryImageEntities.stream().map(entity -> {
           GalleryImageDto imageDto = new GalleryImageDto();
           imageDto.setId(entity.getId());
           imageDto.setCreatedAt(entity.getCreatedAt());
           imageDto.setCaption(entity.getCaption());
           imageDto.setImageUrl(entity.getImagePath());
           imageDto.setNgoName(name);
           return imageDto;
        }).toList();
    }
    /* NGO uploads image */
    @Transactional
    public void uploadImage(Long ngoId, MultipartFile file, String caption) throws IOException {
        UserEntity ngo = userRepo.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        Path uploadDir = Paths.get("uploads/ngo-gallery");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        Optional<UserEntity> user = userRepo.findById(ngoId);
        GalleryImageEntity entityImage = new GalleryImageEntity();
        entityImage.setNgo(user.get());
        entityImage.setImagePath("/uploads/ngo-gallery/" + fileName);
        entityImage.setCaption(caption);
        entityImage.setStatus(GalleryStatus.PENDING);

        galleryRepo.save(entityImage);

    }

//    /* Platform gallery */
//    public List<GalleryImageDto> getApprovedImages() {
//        return galleryRepo.findByApprovedTrue()
//                .stream()
//                .map(this::toDto)
//                .toList();
//    }
//
//    /* NGO gallery (approved only) */
//    public List<GalleryImageDto> getNgoGallery(Long ngoId) {
//        return galleryRepo.findByNgo_IdAndApprovedTrue(ngoId)
//                .stream()
//                .map(this::toDto)
//                .toList();
//    }
//
//    /* Moderator approval */
//    public void approveImage(Long id) {
//        GalleryImageEntity img = galleryRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Image not found"));
//        img.setApproved(true);
//        galleryRepo.save(img);
//    }

//    public List<String> getImagesByOwnerId(Long ownerId){
//        //for now ngo is the only owner of images
//
//        return galleryRepo.findByNgo_Id(ownerId)
//                .stream()
//                .map(GalleryImageEntity::getImagePath)
//                .toList();
//    }

    public List<GalleryImageDto> getImagesByOwnerId(Long ownerId) {

        Optional<NGOProfileEntity> ngoProfile = ngoProfileRepository.findByUserId(ownerId);
        List<GalleryImageDto> images = new ArrayList<>();
        if(ngoProfile.isPresent()) {
            images = galleryRepo.findByNgo_Id(ownerId)
                    .stream()
                    .map(img -> {
                        GalleryImageDto dto = new GalleryImageDto();
                        dto.setId(img.getId());
                        dto.setImageUrl(img.getImagePath());
                        dto.setCaption(img.getCaption());
                        dto.setNgoName(ngoProfile.get().getNgoName());
                        dto.setStatus(img.getStatus().name());       // PENDING / APPROVED / REJECTED
                        dto.setRejectReason(img.getRejectionReason()); // nullable
                        return dto;
                    })
                    .toList();
        }
        return images;
    }

    private GalleryImageDto toDto(GalleryImageEntity e) {
        Optional<NGOProfileEntity> ngoProfileEntity = ngoProfileRepository.findByUserId(e.getNgo().getId());
        String name;
        if (ngoProfileEntity.isEmpty()) {
            name = e.getNgo().getFullName();
        } else {
            name = ngoProfileEntity.get().getNgoName();
        }

        GalleryImageDto dto = new GalleryImageDto();
        dto.setId(e.getId());
        dto.setImageUrl(e.getImagePath());
        dto.setCaption(e.getCaption());
        dto.setNgoName(name);
        return dto;
    }

}
