package com.prerana.userservice.repository;

import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationCategory;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.HelpType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DonationOfferRepository extends JpaRepository<DonationOfferEntity, Long> {
    List<DonationOfferEntity> findByUserId(Long userId);

    Long countByStatusIn(List<DonationOfferStatus> status);

    Optional<DonationOfferEntity> findByIdAndStatusIn(Long id,List<DonationOfferStatus> status);
    @Query("""
        SELECT COUNT(d)
        FROM DonationOfferEntity d
        WHERE d.status = :status
          AND d.updatedAt BETWEEN :start AND :end
    """)
    long countCompletedToday(
            @Param("status") DonationOfferStatus status,
            @Param("start") LocalDateTime startOfDay,
            @Param("end") LocalDateTime endOfDay
    );

    @Query("""
        SELECT d FROM DonationOfferEntity d
        JOIN d.user u
        WHERE
          (:search IS NULL OR
             u.fullName ILIKE CONCAT('%', CAST(:search as text) , '%')
             OR d.reason ILIKE CONCAT('%', CAST(:search as text) , '%')
          )
        AND (:category IS NULL OR d.donationCategory ILIKE :category)
        AND (:type IS NULL OR d.helpType = :type)
        AND (:status IS NULL OR d.status = :status)
    """)
    Page<DonationOfferEntity> search(
            @Param("search") String search,
            @Param("category") DonationCategory category,
            @Param("type") HelpType type,
            @Param("status") DonationOfferStatus status,
            Pageable pageable
    );

    Long countByUser_Id(Long userId);


    @Query("select coalesce(sum(d.amount),0) from DonationOfferEntity d where d.user.id = :userId")
    Long sumAmountByUserId(Long userId);

    long countByUser_IdAndStatus(Long userId, DonationOfferStatus status);

    @Query("""
select coalesce(sum(d.amount),0)
from DonationOfferEntity d
where d.user.id = :userId
and d.status = 'COMPLETED'
""")
    Long sumCompletedDonationAmount(@Param("userId") Long userId);

    long countByUser_IdAndStatusIn(Long userId,List<DonationOfferStatus> statuses);

    @Query("""
SELECT COUNT(d)
FROM DonationOfferEntity d
WHERE d.user.id = :userId
AND d.status IN :statuses
AND d.createdAt >= :from
""")
    long countForSubscription(
            @Param("userId") Long userId,
            @Param("statuses") List<DonationOfferStatus> statuses,
            @Param("from") LocalDateTime from
    );
}