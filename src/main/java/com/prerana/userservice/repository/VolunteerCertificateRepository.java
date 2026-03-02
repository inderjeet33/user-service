package com.prerana.userservice.repository;

import com.prerana.userservice.entity.VolunteerCertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerCertificateRepository extends JpaRepository<VolunteerCertificateEntity,Long> {

    boolean existsByAssignment_Id(Long assignmentId);

    Optional<VolunteerCertificateEntity> findByAssignment_Id(Long assignmentId);
}
