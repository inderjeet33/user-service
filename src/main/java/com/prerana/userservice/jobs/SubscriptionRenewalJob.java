package com.prerana.userservice.jobs;

import com.prerana.userservice.entity.SubscriptionPlanEntity;
import com.prerana.userservice.entity.UserSubscriptionEntity;
import com.prerana.userservice.enums.SubscriptionStatus;
import com.prerana.userservice.repository.SubscriptionPlanRepository;
import com.prerana.userservice.repository.UserSubscriptionRepository;
import com.prerana.userservice.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionRenewalJob {

    private final UserSubscriptionRepository userSubRepo;
    private final SubscriptionPlanRepository planRepo;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // every hour (safe)
    public void autoRenewSubscriptions() {

        LocalDateTime now = LocalDateTime.now();

        List<UserSubscriptionEntity> expiredSubs =
                userSubRepo.findByActiveTrueAndEndDateBefore(now);

        for (UserSubscriptionEntity sub : expiredSubs) {

            sub.setActive(false);
            sub.setStatus(SubscriptionStatus.EXPIRED);
            userSubRepo.save(sub);

            SubscriptionPlanEntity plan = sub.getPlan();

            // ---------- FREE PLAN AUTO-RENEW ----------
            if (plan.getCode().equalsIgnoreCase("FREE")) {

                UserSubscriptionEntity renewed = UserSubscriptionEntity.builder()
                        .user(sub.getUser())
                        .plan(plan)
                        .status(SubscriptionStatus.ACTIVE)
                        .startDate(now)
                        .endDate(now.plusDays(plan.getDurationDays()))
                        .active(true)
                        .build();

                userSubRepo.save(renewed);
            }

            // ---------- PAID PLAN FALLBACK ----------
            else {
                SubscriptionPlanEntity freePlan =
                        planRepo.findByCodeAndUserType("FREE", sub.getUser().getUserType())
                                .orElseThrow();

                UserSubscriptionEntity fallback = UserSubscriptionEntity.builder()
                        .user(sub.getUser())
                        .plan(freePlan)
                        .status(SubscriptionStatus.ACTIVE)
                        .startDate(now)
                        .endDate(now.plusDays(freePlan.getDurationDays()))
                        .active(true)
                        .build();

                userSubRepo.save(fallback);
                notificationService.notify(
                        sub.getUser(),
                        "Subscription Expired",
                        "Your subscription has expired. Upgrade to continue premium benefits."
                );
            }


        }
    }
}