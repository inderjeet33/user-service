package com.prerana.userservice.repository;

import com.prerana.userservice.entity.CampaignEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity,Long> {
    List<CampaignEntity> findByOwner(UserEntity owner);
    long countByOwner_Id(Long ngoId);

    long countByOwner_IdAndStatus(Long ngoId, CampaignStatus status);

    List<CampaignEntity> findByStatus(CampaignStatus status);

//    List<CampaignEntity> findByStatusOrderByPriority


    Long countByStatus(CampaignStatus status);

    Optional<CampaignEntity> findByOwner_id(Long id);

    long countByOwner_IdAndStatusIn(Long ownerId,List<CampaignStatus> campaignStatuses);

//    @Query("""
//    SELECT c
//    FROM CampaignEntity c
//    JOIN c.owner ngo
//    JOIN UserSubscriptionEntity us
//         ON us.user = ngo.user AND us.active = true
//    JOIN SubscriptionPlanEntity sp
//         ON sp = us.plan
//    WHERE c.status = :status
//    ORDER BY sp.priority DESC, c.createdAt DESC
//""")
//    List<CampaignEntity> findCampaignsByStatusOrderByPriority(
//            @Param("status") CampaignStatus status
//    );


        @Query("""
        SELECT c
        FROM CampaignEntity c
        JOIN UserSubscriptionEntity us
            ON us.user = c.owner
        JOIN SubscriptionPlanEntity sp
            ON sp = us.plan
        WHERE c.status = :status
          AND us.active = true
        ORDER BY sp.priority DESC, c.createdAt DESC
    """)
        List<CampaignEntity> findByStatusWithNgoPriority(
                @Param("status") CampaignStatus status
        );



    List<CampaignEntity> findByStatusOrderByPriorityDescCreatedAtDesc(
            CampaignStatus status
    );

}
