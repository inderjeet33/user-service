package com.prerana.userservice.service;

import com.prerana.userservice.dto.SubscriptionPlanDto;
import com.prerana.userservice.dto.UserSubscriptionDto;
import com.prerana.userservice.entity.SubscriptionFeatureEntity;
import com.prerana.userservice.entity.SubscriptionPlanEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.entity.UserSubscriptionEntity;
import com.prerana.userservice.enums.FeatureKey;
import com.prerana.userservice.enums.SubscriptionStatus;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.exceptions.SubscriptionLimitExceededException;
import com.prerana.userservice.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private  UserSubscriptionRepository userSubRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private  SubscriptionFeatureRepository featureRepo;

    @Autowired
    private HelpRequestAssignmentRepository helpRequestAssignmentRepository;

    @Autowired
    private SubscriptionPlanRepository planRepo;

    public int getLimitV1(Long userId, FeatureKey key) {
        return featureRepo.findFeatureValue(userId, key)
                .map(v -> "UNLIMITED".equals(v) ? Integer.MAX_VALUE : Integer.parseInt(v))
                .orElse(0);
    }

    public int getLimitV2(Long userId, FeatureKey key) {

        UserSubscriptionEntity sub = getOrCreateActiveSubscription(userId);

        return featureRepo.findByPlan_IdAndFeatureKey(
                        sub.getPlan().getId(), key
                )
                .map(f -> "UNLIMITED".equalsIgnoreCase(f.getFeatureValue())
                        ? Integer.MAX_VALUE
                        : Integer.parseInt(f.getFeatureValue()))
                .orElse(0);
    }


//    public int getLimitV2(Long userId, FeatureKey key) {
//
//        UserSubscriptionEntity sub =
//                userSubRepo.findFirstByUser_IdAndActiveTrue(userId)
//                        .orElseThrow(() -> new RuntimeException("No active subscription"));
//
//        return featureRepo.findByPlan_IdAndFeatureKey(
//                        sub.getPlan().getId(), key
//                )
//                .map(f -> "UNLIMITED".equals(f.getFeatureValue())
//                        ? Integer.MAX_VALUE
//                        : Integer.parseInt(f.getFeatureValue()))
//                .orElse(0);
//    }
//    public boolean hasFeature(Long userId, FeatureKey key) {
//        return featureRepo.findFeatureValue(userId, key)
//                .map(v -> v.equalsIgnoreCase("true"))
//                .orElse(false);
//    }

    public boolean hasFeature(Long userId, FeatureKey key) {

        UserSubscriptionEntity sub = getOrCreateActiveSubscription(userId);

        return featureRepo.findByPlan_IdAndFeatureKey(
                sub.getPlan().getId(), key
        ).isPresent();
    }


    public boolean hasFeatureV2(Long userId, FeatureKey key) {

        UserSubscriptionEntity sub =
                userSubRepo.findFirstByUser_IdAndActiveTrue(userId)
                        .orElseThrow(() -> new RuntimeException("No active subscription"));

        return featureRepo.findByPlan_IdAndFeatureKey(
                        sub.getPlan().getId(), key
                )
                .map(f -> f.getFeatureValue().equalsIgnoreCase("true"))
                .orElse(false);
    }

    public List<SubscriptionPlanDto> getPlans(UserType userType) {

        return planRepo.findByUserTypeAndActiveTrue(userType)
                .stream()
                .map(this::mapPlan)
                .toList();
    }

    public List<SubscriptionPlanDto> getAllPlans(Long userId) {

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserType userType = user.getUserType();
        List<SubscriptionPlanEntity> plans =
                planRepo.findByUserTypeAndActiveTrue(userType);

        return plans.stream().map(plan -> {

            List<SubscriptionFeatureEntity> featureEntities =
                    featureRepo.findByPlan_Id(plan.getId());

            Map<String, String> features = featureEntities.stream()
                    .collect(Collectors.toMap(
                            f -> f.getFeatureKey().name(),
                            SubscriptionFeatureEntity::getFeatureValue
                    ));

            SubscriptionPlanDto dto = new SubscriptionPlanDto();
            dto.setId(plan.getId());
            dto.setCode(plan.getCode());
            dto.setCreatedAt(plan.getCreatedAt());
            dto.setUpdatedAt(plan.getUpdatedAt());
            dto.setPrice(plan.getPrice());
            dto.setDurationDays(plan.getDurationDays());
            dto.setFeatures(features);
//dto.setFeatures(features);            return SubscriptionPlanDto.builder()
//                    .code(plan.getCode())
//                    .price(plan.getPrice())
//                    .durationDays(plan.getDurationDays())
//                    .features(features)
//                    .build();

            return dto;
        }).toList();
    }
    /* -------------------- CURRENT SUB -------------------- */
//    public UserSubscriptionDto getMySubscription(Long userId) {
//
//        UserSubscriptionEntity sub =
//                userSubRepo.findFirstByUser_IdAndActiveTrue(userId)
//                        .orElseThrow(() ->
//                                new RuntimeException("No active subscription"));
//
//        UserSubscriptionDto dto = new UserSubscriptionDto();
//        dto.setPlanCode(sub.getPlan().getCode());
//        dto.setStatus(sub.getStatus());
//        dto.setStartDate(sub.getStartDate());
//        dto.setEndDate(sub.getEndDate());
//
//        return dto;
//    }


    @Transactional
    public void expireSubscriptions() {

        List<UserSubscriptionEntity> expired =
                userSubRepo.findByActiveTrueAndEndDateBefore(LocalDateTime.now());

        for (UserSubscriptionEntity sub : expired) {
            sub.setActive(false);
            sub.setStatus(SubscriptionStatus.EXPIRED);
            userSubRepo.save(sub);
        }
    }


    public UserSubscriptionDto getMySubscription(Long userId) {

        UserSubscriptionEntity sub = getOrCreateActiveSubscription(userId);

        UserSubscriptionDto dto = new UserSubscriptionDto();
        dto.setPlanCode(sub.getPlan().getCode());
        dto.setStatus(sub.getStatus());
        dto.setStartDate(sub.getStartDate());
        dto.setEndDate(sub.getEndDate());

        return dto;
    }


    public SubscriptionPlanEntity getActivePlanOrFree(Long userId, UserType userType) {

        return userSubRepo.findFirstByUser_IdAndActiveTrue(userId)
                .filter(sub -> sub.getEndDate().isAfter(LocalDateTime.now()))
                .map(UserSubscriptionEntity::getPlan)
                .orElseGet(() ->
                        planRepo.findByCodeAndUserType("FREE", userType)
                                .orElseThrow(() ->
                                        new RuntimeException("FREE plan not configured")));
    }


    public int getNgoPriority(Long ngoUserId) {
        return getActivePlanForUser(ngoUserId).getPriority();
    }

    public SubscriptionPlanEntity getActivePlanForUser(Long userId) {

        UserSubscriptionEntity sub =
                userSubRepo.findFirstByUser_IdAndActiveTrue(userId)
                        .orElseThrow(() ->
                                new RuntimeException("No active subscription"));

        // auto-expire safeguard
        if (sub.getEndDate().isBefore(LocalDateTime.now())) {
            sub.setActive(false);
            sub.setStatus(SubscriptionStatus.EXPIRED);
            userSubRepo.save(sub);
            throw new RuntimeException("Subscription expired");
        }

        return sub.getPlan();
    }



    @Transactional
    public void subscribe(Long userId, String planCode) {

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SubscriptionPlanEntity plan =
                planRepo.findByCodeAndUserType(planCode, user.getUserType())
                        .orElseThrow(() ->
                                new RuntimeException("Plan not found"));

        // deactivate existing
        userSubRepo.findFirstByUser_IdAndActiveTrue(userId)
                .ifPresent(existing -> {
                    existing.setActive(false);
                    existing.setStatus(SubscriptionStatus.EXPIRED);
                    userSubRepo.save(existing);
                });

        UserSubscriptionEntity sub = UserSubscriptionEntity.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now()
                        .plusDays(plan.getDurationDays()))
                .active(true)
                .build();

        userSubRepo.save(sub);
    }

    /* -------------------- MAPPER -------------------- */
    private SubscriptionPlanDto mapPlan(SubscriptionPlanEntity plan) {

        List<SubscriptionFeatureEntity> features =
                featureRepo.findByPlan_Id(plan.getId());

        Map<String, String> map = features.stream()
                .collect(Collectors.toMap(
                        f -> f.getFeatureKey().name(),
                        SubscriptionFeatureEntity::getFeatureValue
                ));

        SubscriptionPlanDto dto = new SubscriptionPlanDto();
        dto.setCode(plan.getCode());
        dto.setPrice(plan.getPrice());
        dto.setDurationDays(plan.getDurationDays());
        dto.setFeatures(map);

        return dto;
    }

//    public void upgradePlan(Long userId, String planCode) {
//
//        UserEntity user = userRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        SubscriptionPlanEntity newPlan = planRepo.findByCode(planCode)
//                .orElseThrow(() -> new RuntimeException("Plan not found"));
//
//        // Deactivate old subscription
//        userSubRepo.deactivateAll(userId);
//
//        // Create new subscription
//        UserSubscriptionEntity sub = UserSubscriptionEntity.builder()
//                .user(user)
//                .plan(newPlan)
//                .status(SubscriptionStatus.ACTIVE)
//                .build();
//
//        userSubRepo.save(sub);
//    }

    @Transactional
    public void upgradePlan(Long userId, String planCode) {

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SubscriptionPlanEntity newPlan =
                planRepo.findByCodeAndUserType(planCode, user.getUserType())
                        .orElseThrow(() -> new RuntimeException("Plan not found"));

        // deactivate existing
        userSubRepo.findFirstByUser_IdAndActiveTrue(userId)
                .ifPresent(sub -> {
                    sub.setActive(false);
                    sub.setStatus(SubscriptionStatus.EXPIRED);
                    userSubRepo.save(sub);
                });

        UserSubscriptionEntity newSub = UserSubscriptionEntity.builder()
                .user(user)
                .plan(newPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(newPlan.getDurationDays()))
                .active(true)
                .build();

        userSubRepo.save(newSub);
    }

    @Transactional
    public UserSubscriptionEntity getOrCreateActiveSubscription(Long userId) {

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Check existing active subscription
        Optional<UserSubscriptionEntity> opt =
                userSubRepo.findFirstByUser_IdAndActiveTrue(userId);

        if (opt.isPresent()) {
            UserSubscriptionEntity sub = opt.get();

            // 2. Auto-expire if endDate passed
            if (sub.getEndDate().isBefore(LocalDateTime.now())) {
                sub.setActive(false);
                sub.setStatus(SubscriptionStatus.EXPIRED);
                userSubRepo.save(sub);
            } else {
                return sub; // valid subscription
            }
        }

        // 3. Fallback to FREE plan
        SubscriptionPlanEntity freePlan =
                planRepo.findByCodeAndUserType("FREE", user.getUserType())
                        .orElseThrow(() ->
                                new RuntimeException("FREE plan missing for " + user.getUserType()));

        UserSubscriptionEntity freeSub = UserSubscriptionEntity.builder()
                .user(user)
                .plan(freePlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(freePlan.getDurationDays()))
                .active(true)
                .build();

        return userSubRepo.save(freeSub);
    }

    public void validateCsrHelpLimit(Long csrUserId) {

        UserSubscriptionEntity sub =
                userSubRepo.findFirstByUser_IdAndActiveTrue(csrUserId)
                        .orElseThrow(() ->
                                new RuntimeException("CSR has no active subscription"));

        int limit = featureRepo
                .findByPlan_IdAndFeatureKey(
                        sub.getPlan().getId(),
                            FeatureKey.CSR_MAX_ACTIVE_ASSIGNMENTS
                )
                .map(f -> "UNLIMITED".equals(f.getFeatureValue())
                        ? Integer.MAX_VALUE
                        : Integer.parseInt(f.getFeatureValue()))
                .orElse(0);

        if (limit == Integer.MAX_VALUE) return;

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        long used =
                helpRequestAssignmentRepository
                        .countAssignmentsForHelperBetween(csrUserId, start, end);

        if (used >= limit) {
            throw new SubscriptionLimitExceededException(
                    "CSR help assignment limit reached"
            );
        }
    }


}

