package com.prerana.userservice.service;
import com.prerana.userservice.certificate.CertificatePdfGenerator;
import com.prerana.userservice.dto.AssignmentHistoryDto;
import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.dto.ModeratorAssignmentDto;
import com.prerana.userservice.entity.DonationCertificateEntity;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.mapper.ModeratorAssignmentMapper;
import com.prerana.userservice.repository.CertificateRepository;
import com.prerana.userservice.repository.DonationOfferRepository;
import com.prerana.userservice.dto.ModeratorAssignmentRequestDTO;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.repository.ModeratorAssignmentRepository;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ast.tree.update.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModeratorAssignmentService {

    @Autowired
    private final ModeratorAssignmentRepository repository;
    @Autowired
    private final DonationOfferRepository donationRepo;
    @Autowired
    private final UserRepository userRepo;

    @Autowired
    private ModeratorAssignmentMapper mapperUtil;

    @Autowired
    private CertificateRepository certificateRepo;


    public List<AssignmentHistoryDto> getAssignmentHistoryForDoantionId(Long donationRequestId){
        List<ModeratorAssignmentEntity> assignments = repository.findByDonationRequest_IdOrderByCreatedAtDesc(donationRequestId);
        return assignments.stream()
                .map(a -> new AssignmentHistoryDto(
                        a.getId(),
                        a.getReceiver().getFullName(),
                        a.getStatus(),
                        a.getCreatedAt()
                ))
                .toList();
    }

//    @Transactional
//    public ModeratorAssignmentEntity assignNgos(Long moderatorId, ModeratorAssignmentRequestDTO req) {
//
//        DonationOfferEntity dr = donationRepo.findById(req.getDonationRequestId())
//                .orElseThrow(() -> new RuntimeException("Donation request not found"));
//
//        UserEntity receiver = userRepo.findById(req.getReceiverId())
//                .orElseThrow(() -> new RuntimeException("NGO not found"));
//
//        UserEntity moderator = userRepo.findById(moderatorId)
//                .orElseThrow(() -> new RuntimeException("Moderator not found"));
//
//        UserEntity donor = dr.getUser(); // donation_request already has donor
//        if (receiver.getUserType() != UserType.NGO &&
//                receiver.getUserType() != UserType.INDIVIDUAL) {
//
//            throw new RuntimeException("Receiver must be NGO or Individual Receiver.");
//        }
//
//        if(dr.getStatus() == DonationOfferStatus.OPEN) {
//
//            if (repository.existsByDonationRequest_IdAndStatusIn(req.getDonationRequestId(), List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.IN_PROGRESS))) {
//                throw new RuntimeException("Donation offer already assigned.");
//            }
//
//
////        if (moderator.getUserType() != UserType.MODERATOR) {
////            throw new RuntimeException("Only moderators can create assignments.");
////        }
////        if (donor.getId().equals(receiver.getId())) {
////            throw new RuntimeException("Donor cannot be their own receiver.");
////        }
////
//            ModeratorAssignmentEntity assignment = ModeratorAssignmentEntity.builder()
//                    .moderator(moderator)
//                    .donationRequest(dr)
//                    .receiver(receiver)
//                    .receiver_type(receiver.getUserType())
//                    .donor(donor)
//                    .status(AssignmentStatus.ASSIGNED)
//                    .build();
//
//            dr.setStatus(DonationOfferStatus.ASSIGNED);
//            donationRepo.save(dr);
//            return repository.save(assignment);
//        }else if(dr.getStatus() == DonationOfferStatus.ASSIGNED){
//            ModeratorAssignmentEntity currentAssignment =
//                    repository.findFirstByDonationRequest_IdAndStatus(
//                            dr.getId(),
//                            AssignmentStatus.ASSIGNED
//                    ).orElseThrow(() -> new RuntimeException("No active assignment found"));
//            currentAssignment.setStatus(AssignmentStatus.REASSIGNED);
//            repository.save(currentAssignment);
//
//            // create new assignment
//            ModeratorAssignmentEntity newAssignment =
//                    ModeratorAssignmentEntity.builder()
//                            .moderator(moderator)
//                            .donationRequest(dr)
//                            .receiver(receiver)
//                            .receiver_type(receiver.getUserType())
//                            .donor(donor)
//                            .status(AssignmentStatus.ASSIGNED)
//                            .build();
//
//            return repository.save(newAssignment);
//        }else{
//            // in case of any other state, we can not assign it to someone else
//            throw new RuntimeException("Cannot assign donation offer in status : "+dr.getStatus().name());
//        }
//    }

    public Optional<ModeratorAssignmentEntity> getById(Long id) {
        return repository.findById(id);
    }

//    public ModeratorAssignmentEntity updateStatus(Long id, AssignmentStatus status) {
//        ModeratorAssignmentEntity ma = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Assignment not found"));
//        ma.setStatus(status);
//        return repository.save(ma);
//    }

    @Transactional
    public ModeratorAssignmentEntity assignNgo(Long moderatorId, ModeratorAssignmentRequestDTO req) {

        DonationOfferEntity dr = donationRepo.findById(req.getDonationRequestId())
                .orElseThrow(() -> new RuntimeException("Donation request not found"));

        UserEntity receiver = userRepo.findById(req.getReceiverId())
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        UserEntity moderator = userRepo.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Moderator not found"));

        UserEntity donor = dr.getUser();

        if (receiver.getUserType() != UserType.NGO && receiver.getUserType() != UserType.INDIVIDUAL) {
            throw new RuntimeException("Receiver must be NGO or Individual Receiver.");
        }

        // ❌ Block invalid offer states
        if (dr.getStatus() == DonationOfferStatus.IN_PROGRESS ||
                dr.getStatus() == DonationOfferStatus.COMPLETED ||
                dr.getStatus() == DonationOfferStatus.CANCELLED) {

            throw new RuntimeException("Cannot assign donation offer in status: " + dr.getStatus());
        }

        // Check if there is an active assignment
        Optional<ModeratorAssignmentEntity> activeAssignmentOpt =
                repository.findFirstByDonationRequest_IdAndStatusIn(
                        dr.getId(),
                        List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.IN_PROGRESS)
                );

        if (dr.getStatus() == DonationOfferStatus.OPEN) {

            if (activeAssignmentOpt.isPresent()) {
                throw new RuntimeException("Donation offer already assigned.");
            }

            ModeratorAssignmentEntity assignment = ModeratorAssignmentEntity.builder()
                    .moderator(moderator)
                    .donationRequest(dr)
                    .receiver(receiver)
                    .receiver_type(receiver.getUserType())
                    .donor(donor)
                    .status(AssignmentStatus.ASSIGNED)
                    .build();

            dr.setStatus(DonationOfferStatus.ASSIGNED);
            donationRepo.save(dr);

            return repository.save(assignment);
        }

        if (dr.getStatus() == DonationOfferStatus.ASSIGNED) {

            ModeratorAssignmentEntity currentAssignment =
                    repository.findFirstByDonationRequest_IdAndStatus(
                            dr.getId(),
                            AssignmentStatus.ASSIGNED
                    ).orElseThrow(() -> new RuntimeException("No active assignment found"));

            // Only allow reassignment if NGO rejected or expired
            if (currentAssignment.getStatus() != AssignmentStatus.REJECTED_BY_RECEIVER &&
                    currentAssignment.getStatus() != AssignmentStatus.EXPIRED) {

                throw new RuntimeException("Cannot reassign. NGO has not rejected or expired yet.");
            }

            currentAssignment.setStatus(AssignmentStatus.REASSIGNED);
            repository.save(currentAssignment);

            ModeratorAssignmentEntity newAssignment = ModeratorAssignmentEntity.builder()
                    .moderator(moderator)
                    .donationRequest(dr)
                    .receiver(receiver)
                    .receiver_type(receiver.getUserType())
                    .donor(donor)
                    .status(AssignmentStatus.ASSIGNED)
                    .build();

            return repository.save(newAssignment);
        }

        throw new RuntimeException("Invalid donation offer state.");
    }


    @Transactional
    public ModeratorAssignmentEntity updateStatus(Long assignmentId, AssignmentStatus newStatus) {

        ModeratorAssignmentEntity assignment = repository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        DonationOfferEntity offer = assignment.getDonationRequest();

        assignment.setStatus(newStatus);

        switch (newStatus) {
            case IN_PROGRESS -> {
                offer.setStatus(DonationOfferStatus.IN_PROGRESS);
            }

            case COMPLETED -> {
                offer.setStatus(DonationOfferStatus.COMPLETED);
            }

            case CANCELLED_BY_DONOR, REJECTED_BY_RECEIVER, EXPIRED -> {
                offer.setStatus(DonationOfferStatus.OPEN);
            }

        }

        donationRepo.save(offer);
        if(AssignmentStatus.COMPLETED.equals(newStatus)){
            generateCertificateEntity(offer.getAmount(),assignment);
        }
        return repository.save(assignment);
    }

    public void generateCertificateEntity(Long offerAmount,ModeratorAssignmentEntity assignmentEntity) {
        // 🔐 Create certificate ONLY ONCE
        if (!certificateRepo.existsByAssignment_Id(assignmentEntity.getId())) {

            DonationCertificateEntity cert =
                    DonationCertificateEntity.builder()
                            .assignment(assignmentEntity)
                            .donor(assignmentEntity.getDonor())
                            .receiver(assignmentEntity.getReceiver())
                            .donationAmount(offerAmount)
                            .issuedDate(LocalDate.now())
                            .build();

            cert = certificateRepo.save(cert);

            cert.setCertificateId(CertificatePdfGenerator.generateCertificateId(cert.getId()));
            certificateRepo.save(cert);
        }
    }

    public Page<ModeratorAssignmentDto> listPaginatedWithFilter(int page, int size, AssignmentStatus status){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ModeratorAssignmentEntity> data;

        if (status != null) {
            data = repository.findByStatus(status, pageable);
        } else {
            data = repository.findAll(pageable);
        }
        Page<ModeratorAssignmentDto> pageDtos = mapperUtil.toDtoPage(data);
//        populateReceiverDetailsInDto(pageDtos);
        return pageDtos;
    }
}
