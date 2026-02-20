package com.prerana.userservice.repository;

import com.prerana.userservice.entity.SubscriptionFeatureEntity;
import com.prerana.userservice.enums.FeatureKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionFeatureRepository
        extends JpaRepository<SubscriptionFeatureEntity, Long> {

    @Query("""
        select f.featureValue
        from SubscriptionFeatureEntity f
        join UserSubscriptionEntity us on us.plan = f.plan
        where us.user.id = :userId
          and us.status = 'ACTIVE'
          and f.featureKey = :key
    """)
    Optional<String> findFeatureValue(
            @Param("userId") Long userId,
            @Param("key") FeatureKey key
    );

    Optional<SubscriptionFeatureEntity> findByPlan_IdAndFeatureKey(Long planId,FeatureKey featureKey);

    List<SubscriptionFeatureEntity> findByPlan_Id(Long planId);
}
