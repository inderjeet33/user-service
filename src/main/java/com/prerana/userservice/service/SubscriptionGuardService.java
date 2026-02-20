package com.prerana.userservice.service;

import com.prerana.userservice.entity.SubscriptionFeatureEntity;
import com.prerana.userservice.entity.UserSubscriptionEntity;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.FeatureKey;
import com.prerana.userservice.exceptions.SubscriptionLimitExceededException;
import com.prerana.userservice.repository.DonationOfferRepository;
import com.prerana.userservice.repository.HelpRequestRepository;
import com.prerana.userservice.repository.SubscriptionFeatureRepository;
import com.prerana.userservice.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionGuardService {

    @Autowired
    private final UserSubscriptionRepository subscriptionRepo;

    @Autowired
    private final SubscriptionFeatureRepository featureRepo;

    @Autowired
    private final HelpRequestRepository helpRequestRepo;

    @Autowired
    private DonationOfferRepository donationOfferRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    public void checkHelpRequestAllowed(Long userId) {

        UserSubscriptionEntity sub =
                subscriptionRepo.findByUser_IdAndActiveTrue(userId)
                        .orElseThrow(() -> new RuntimeException("No active plan"));

        SubscriptionFeatureEntity feature =
                featureRepo.findByPlan_IdAndFeatureKey(
                        sub.getPlan().getId(),
                        FeatureKey.HELP_REQUEST_LIMIT
                ).orElseThrow();

        if ("UNLIMITED".equals(feature.getFeatureValue())) return;

        long used =
                helpRequestRepo.countByUser_Id(userId);

        int limit = Integer.parseInt(feature.getFeatureValue());

        if (used >= limit) {
            throw new SubscriptionLimitExceededException(
                    "Help request limit exceeded. Upgrade plan."
            );
        }
    }

    public void checkDonationOfferCreation(Long userId){
        List<DonationOfferStatus> COUNTABLE_STATUSES = List.of(
                DonationOfferStatus.OPEN,
                DonationOfferStatus.ASSIGNED,
                DonationOfferStatus.IN_PROGRESS,
                DonationOfferStatus.COMPLETED
        );
        LocalDateTime from = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();

        long usage = donationOfferRepository.countForSubscription(
                userId,
                COUNTABLE_STATUSES,
                from
        );

        int limit = subscriptionService.getLimitV2(
                userId,
                FeatureKey.DONATION_LIMIT
        );

        if (usage >= limit) {
            throw new SubscriptionLimitExceededException(
                    "Upgrade your plan to create more donation offers"
            );
        }
    }
//    public void checkDonationOfferCreation(Long userId) {
//
//        UserSubscriptionEntity sub =
//                subscriptionRepo.findByUser_IdAndActiveTrue(userId)
//                        .orElseThrow(() -> new RuntimeException("No active plan"));
//
//        SubscriptionFeatureEntity feature =
//                featureRepo.findByPlan_IdAndFeatureKey(
//                        sub.getPlan().getId(),
//                        FeatureKey.DONATION_LIMIT
//                ).orElseThrow();
//
//        if ("UNLIMITED".equals(feature.getFeatureValue())) return;
//
//        long used =
//                donationOfferRepository.countByUser_Id(userId);
//
//        int limit = Integer.parseInt(feature.getFeatureValue());
//
//        if (used >= limit) {
//            throw new SubscriptionLimitExceededException(
//                    "Help request limit exceeded. Upgrade plan."
//            );
//        }
//    }
}
