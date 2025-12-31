package com.prerana.userservice.repository;

import com.prerana.userservice.dto.DonationCertificateDto;
import com.prerana.userservice.entity.DonationCertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<DonationCertificateEntity,Long> {

    public boolean existsByAssignment_Id(Long assignmentId);

    public Optional<DonationCertificateEntity> findByAssignment_Id(Long assignmentId);

    public List<DonationCertificateEntity> findByAssignment_IdIn(List<Long> assignmentId);

}
