package com.prerana.userservice.repository;

import com.prerana.userservice.entity.SubscriptionPlanEntity;
import com.prerana.userservice.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanEntity,Long> {

    Optional<SubscriptionPlanEntity> findByCodeAndUserType(String code, UserType userType);

    List<SubscriptionPlanEntity> findByUserTypeAndActiveTrue(UserType userType);

    Optional<SubscriptionPlanEntity> findByCode(String planCode);
}
