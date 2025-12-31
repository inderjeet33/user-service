package com.prerana.userservice.repository;

import com.prerana.userservice.entity.CampaignEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity,Long> {
    List<CampaignEntity> findByOwner(UserEntity owner);
    long countByOwner_Id(Long ngoId);

    long countByOwner_IdAndStatus(Long ngoId, CampaignStatus status);

    List<CampaignEntity> findByStatus(CampaignStatus status);

    Long countByStatus(CampaignStatus status);
}
