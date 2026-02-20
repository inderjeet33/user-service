package com.prerana.userservice.repository;

import com.prerana.userservice.entity.UserSubscriptionEntity;
import com.prerana.userservice.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository
        extends JpaRepository<UserSubscriptionEntity, Long> {

    Optional<UserSubscriptionEntity>
    findByUser_IdAndStatus(Long userId, SubscriptionStatus status);


    Optional<UserSubscriptionEntity>
    findByUser_IdAndActiveTrue(Long userId);

    Optional<UserSubscriptionEntity> findFirstByUser_IdAndActiveTrue(Long userId);

    Optional<UserSubscriptionEntity> findActiveByUserId(Long userId);

    @Modifying
    @Query("update UserSubscriptionEntity u set u.status='INACTIVE' where u.user.id=:userId")
    void deactivateAll(Long userId);

    @Modifying
    @Query("""
        update UserSubscriptionEntity us
        set us.active = false,
            us.status = com.prerana.userservice.enums.SubscriptionStatus.EXPIRED
        where us.user.id = :userId
        and us.active = true
    """)
    void expireActiveSubscription(@Param("userId") Long userId);

    List<UserSubscriptionEntity> findByActiveTrueAndEndDateBefore(LocalDateTime localDateTime);
}


