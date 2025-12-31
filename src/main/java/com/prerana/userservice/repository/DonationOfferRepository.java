package com.prerana.userservice.repository;

import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
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
}