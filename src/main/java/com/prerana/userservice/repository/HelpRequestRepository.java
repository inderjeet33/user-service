package com.prerana.userservice.repository;

import com.prerana.userservice.entity.HelpRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HelpRequestRepository
        extends JpaRepository<HelpRequestEntity, Long> {

    List<HelpRequestEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Long countByUser_Id(Long userId);

}
