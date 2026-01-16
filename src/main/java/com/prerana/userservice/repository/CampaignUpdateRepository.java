package com.prerana.userservice.repository;

import com.prerana.userservice.entity.CampaignUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignUpdateRepository
        extends JpaRepository<CampaignUpdateEntity, Long> {

    List<CampaignUpdateEntity> findByCampaignIdOrderByCreatedAtDesc(Long campaignId);
}
