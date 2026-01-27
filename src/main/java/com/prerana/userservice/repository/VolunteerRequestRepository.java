package com.prerana.userservice.repository;

import com.prerana.userservice.entity.VolunteerRequestEntity;
import com.prerana.userservice.enums.VolunteerOfferStatus;
import com.prerana.userservice.enums.VolunteerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerRequestRepository extends JpaRepository<VolunteerRequestEntity, Long> {

    List<VolunteerRequestEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);

    @Query("""
        SELECT d FROM VolunteerRequestEntity d
        JOIN d.user u
        WHERE
          (:search IS NULL OR
             u.fullName ILIKE CONCAT('%', CAST(:search as text) , '%')
             OR d.reason ILIKE CONCAT('%', CAST(:search as text) , '%')
          )
        AND (:type IS NULL OR d.volunteerType ILIKE :type)
        AND (:status IS NULL OR d.status = :status)
    """)
    Page<VolunteerRequestEntity> search(String search, VolunteerType type, VolunteerOfferStatus status, Pageable pageable);
}
