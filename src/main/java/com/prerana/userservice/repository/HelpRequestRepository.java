package com.prerana.userservice.repository;

import com.prerana.userservice.entity.HelpRequestEntity;
import com.prerana.userservice.enums.HelpRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HelpRequestRepository
        extends JpaRepository<HelpRequestEntity, Long> {

    List<HelpRequestEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Long countByUser_Id(Long userId);

    Long countByUser_IdAndStatusIn(Long userId, List<HelpRequestStatus> status);

    List<HelpRequestEntity> findByUser_IdAndStatusInOrderByCreatedAtDesc(Long userId,List<HelpRequestStatus> status);

    Long countByUser_IdAndStatus(Long userId,HelpRequestStatus helpRequestStatus);
}
