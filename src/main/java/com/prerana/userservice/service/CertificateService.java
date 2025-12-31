package com.prerana.userservice.service;

import com.prerana.userservice.certificate.CertificatePdfGenerator;
import com.prerana.userservice.dto.DonationCertificateDto;
import com.prerana.userservice.entity.DonationCertificateEntity;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.repository.CertificateRepository;
import com.prerana.userservice.repository.DonationOfferRepository;
import com.prerana.userservice.repository.ModeratorAssignmentRepository;
import com.prerana.userservice.repository.NGOProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateService {

    @Autowired
    private final DonationOfferRepository donationRepo;

    @Autowired
    private final ModeratorAssignmentRepository assignmentRepo;

    @Autowired
    private final NGOProfileRepository ngoRepo;

    @Autowired
    private final CertificatePdfGenerator pdfGenerator;

    @Autowired
    private CertificateRepository certificateRepository;


    @Transactional
    public byte[] generateCertificate(Long offerId, Long donorId) {

        DonationOfferEntity offer = donationRepo.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getUser().getId().equals(donorId)) {
            throw new RuntimeException("Not authorized");
        }

        if (offer.getStatus() != DonationOfferStatus.COMPLETED) {
            throw new RuntimeException("Donation not completed");
        }

        ModeratorAssignmentEntity assignment =
                assignmentRepo.findFirstByDonationRequest_IdAndStatus(
                                offerId, AssignmentStatus.COMPLETED)
                        .orElseThrow(() -> new RuntimeException("Assignment not completed"));

        NGOProfileEntity ngo = ngoRepo.findByUser_id(assignment.getReceiver().getId())
                .orElseThrow(() -> new RuntimeException("NGO profile missing"));

        DonationCertificateEntity cert =
                certificateRepository.findByAssignment_Id(assignment.getId())
                        .orElseThrow();

        DonationCertificateDto dto = DonationCertificateDto.builder()
                .certificateId(cert.getCertificateId())
                .donorName(offer.getUser().getFullName())
                .donorEmail(offer.getUser().getEmail())
                .ngoName(ngo.getNgoName())
                .ngoRegistrationNumber(ngo.getRegistrationNumber())
                .donationCategory(offer.getDonationCategory())
                .amount(offer.getAmount())
                .location(offer.getLocation())
                .completedDate(assignment.getUpdatedAt().toLocalDate().toString())
                .platformName("Prerana Helpline Foundation")
                .donationOfferId(offer.getId())
                .build();

        return pdfGenerator.generate(dto);
    }
}
