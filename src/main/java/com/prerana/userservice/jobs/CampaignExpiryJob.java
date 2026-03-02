package com.prerana.userservice.jobs;

import com.prerana.userservice.entity.CampaignEntity;
import com.prerana.userservice.enums.CampaignStatus;
import com.prerana.userservice.repository.CampaignRepository;
import com.prerana.userservice.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignExpiryJob {

    private final CampaignRepository campaignRepository;
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // every hour
    public void expireCampaigns() {

        LocalDateTime now = LocalDateTime.now();

        List<CampaignEntity> expiredCampaigns =
                campaignRepository.findByStatusAndExpiresAtBefore(
                        CampaignStatus.ACTIVE,
                        now
                );

        for (CampaignEntity campaign : expiredCampaigns) {
            campaign.setStatus(CampaignStatus.EXPIRED);
            campaignRepository.save(campaign);

            // Dummy notification
            notificationService.notify(
                    campaign.getOwner(),
                    "Campaign Expired",
                    "Your campaign \"" + campaign.getTitle() + "\" has expired."
            );
        }
    }
}