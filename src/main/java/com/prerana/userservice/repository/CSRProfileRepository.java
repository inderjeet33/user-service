package com.prerana.userservice.repository;

import com.prerana.userservice.entity.CSRProfileEntity;
import com.prerana.userservice.enums.ActivationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CSRProfileRepository
        extends JpaRepository<CSRProfileEntity, Long> {

    Optional<CSRProfileEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
    List<CSRProfileEntity> findByActivationStatus(ActivationStatus status);

}
