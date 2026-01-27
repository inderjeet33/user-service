package com.prerana.userservice.repository;

import com.prerana.userservice.entity.HelpRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HelpRequestRepository
        extends JpaRepository<HelpRequestEntity, Long> {

    List<HelpRequestEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);

}
