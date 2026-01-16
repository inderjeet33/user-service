package com.prerana.userservice.repository;

import com.prerana.userservice.entity.GalleryImageEntity;
import com.prerana.userservice.enums.GalleryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImageEntity, Long> {

//    List<GalleryImageEntity> findByApprovedTrue();
//
//    List<GalleryImageEntity> findByNgo_Id(Long ngoId);
//
//    List<GalleryImageEntity> findByNgo_IdAndApprovedTrue(Long ngoId);

    List<GalleryImageEntity> findByNgo_Id(Long ngoId);

    List<GalleryImageEntity> findByNgo_IdAndStatus(Long ngoId, GalleryStatus status);

}
