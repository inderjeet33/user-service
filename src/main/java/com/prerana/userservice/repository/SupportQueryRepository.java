package com.prerana.userservice.repository;

import com.prerana.userservice.entity.SupportQueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportQueryRepository
        extends JpaRepository<SupportQueryEntity, Long> {
}
