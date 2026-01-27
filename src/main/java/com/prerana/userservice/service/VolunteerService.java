package com.prerana.userservice.service;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.entity.VolunteerAssignmentEntity;
import com.prerana.userservice.entity.VolunteerRequestEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.enums.VolunteerOfferStatus;
import com.prerana.userservice.enums.VolunteerType;
import com.prerana.userservice.mapper.VolunteerOfferDtoMapper;
import com.prerana.userservice.repository.UserRepository;
import com.prerana.userservice.repository.VolunteerAssignmentRepository;
import com.prerana.userservice.repository.VolunteerRequestRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
//
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VolunteerService {

    @Autowired
    private final VolunteerRequestRepository volunteerRepo;

    @Autowired
    private VolunteerAssignmentRepository assignmentRepo;

    @Autowired
    private final UserRepository userRepo;

    @Autowired
    private NGOProfileService ngoProfileService;

    @Autowired
    private VolunteerOfferDtoMapper volunteerOfferDtoMapper;

    @Transactional
    public VolunteerAssignmentEntity assignNgoForVolunteer(
            Long moderatorId,
            VolunteerAssignmentRequestDTO req
    ) {

        VolunteerRequestEntity vr = volunteerRepo.findById(req.getVolunteerRequestId())
                .orElseThrow(() -> new RuntimeException("Volunteer request not found"));

        UserEntity receiver = userRepo.findById(req.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (receiver.getUserType() != UserType.NGO &&
                receiver.getUserType() != UserType.INDIVIDUAL) {
            throw new RuntimeException("Receiver must be NGO or Individual");
        }

        UserEntity moderator = userRepo.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Moderator not found"));

        UserEntity volunteer = vr.getUser();

        // 5️⃣ Block invalid volunteer request states
        if (vr.getStatus() == VolunteerOfferStatus.IN_PROGRESS ||
                vr.getStatus() == VolunteerOfferStatus.COMPLETED ||
                vr.getStatus() == VolunteerOfferStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cannot assign volunteer request in status: " + vr.getStatus()
            );
        }

        // 6️⃣ Check active assignment
        Optional<VolunteerAssignmentEntity> activeAssignmentOpt =
                assignmentRepo.findFirstByVolunteerRequest_IdAndStatusIn(
                        vr.getId(),
                        List.of(
                                AssignmentStatus.ASSIGNED,
                                AssignmentStatus.IN_PROGRESS
                        )
                );

        if (vr.getStatus() == VolunteerOfferStatus.OPEN) {

            if (activeAssignmentOpt.isPresent()) {
                throw new RuntimeException("Volunteer request already assigned");
            }

            VolunteerAssignmentEntity assignment =
                    VolunteerAssignmentEntity.builder()
                            .moderator(moderator)
                            .volunteerRequest(vr)
                            .receiver(receiver)
                            .receiver_type(receiver.getUserType())
                            .volunteer(volunteer)
                            .status(AssignmentStatus.ASSIGNED)
                            .build();

            vr.setStatus(VolunteerOfferStatus.ASSIGNED);
            volunteerRepo.save(vr);

            return assignmentRepo.save(assignment);
        }

        if (vr.getStatus() == VolunteerOfferStatus.ASSIGNED) {

            VolunteerAssignmentEntity currentAssignment =
                    assignmentRepo
                            .findFirstByVolunteerRequest_IdAndStatus(
                                    vr.getId(),
                                    AssignmentStatus.ASSIGNED
                            )
                            .orElseThrow(() ->
                                    new RuntimeException("No active assignment found")
                            );

            if (currentAssignment.getStatus() != AssignmentStatus.REJECTED_BY_RECEIVER &&
                    currentAssignment.getStatus() != AssignmentStatus.EXPIRED) {

                throw new RuntimeException(
                        "Cannot reassign. NGO has not rejected or expired yet."
                );
            }

            currentAssignment.setStatus(AssignmentStatus.REASSIGNED);
            assignmentRepo.save(currentAssignment);

            VolunteerAssignmentEntity newAssignment =
                    VolunteerAssignmentEntity.builder()
                            .moderator(moderator)
                            .volunteerRequest(vr)
                            .receiver(receiver)
                            .receiver_type(receiver.getUserType())
                            .volunteer(volunteer)
                            .status(AssignmentStatus.ASSIGNED)
                            .build();

            return assignmentRepo.save(newAssignment);
        }

        throw new RuntimeException("Invalid volunteer request state");
    }


    @Transactional
    public VolunteerRequestEntity createVolunteerRequest(Long userId, VolunteerRequestDto dto) {

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        VolunteerRequestEntity request = VolunteerRequestEntity.builder()
                .user(user)
                .volunteerType(dto.getVolunteerType())
                .skills(dto.getSkills())
                .availability(dto.getAvailability())
                .timeLine(dto.getTimeLine())
                .location(dto.getLocation())
                .preferredContact(dto.getPreferredContact())
                .reason(dto.getReason())
                .status(VolunteerOfferStatus.OPEN)
                .build();

        return volunteerRepo.save(request);
    }

    @Transactional
    public void cancelRequest(Long requestId, Long userId) {
        VolunteerRequestEntity req = volunteerRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!req.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        req.setStatus(VolunteerOfferStatus.CANCELLED);
        volunteerRepo.save(req);
    }

    public List<VolunteerRequestEntity> getMyRequests(Long userId) {
        return volunteerRepo.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    public Page<VolunteerOffersRequestDto> search(
            int page, int size, String search, String type, String status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<VolunteerRequestEntity> result =
                volunteerRepo.search(search, StringUtils.isBlank(type)? null : VolunteerType.valueOf(type), StringUtils.isBlank(status) ? null : VolunteerOfferStatus.valueOf(status), pageable);

        Page<VolunteerOffersRequestDto> dtos = result.map(volunteerOfferDtoMapper::toDto);

        populateReceiverDetails(dtos);

        return dtos;
    }

    private void populateReceiverDetails(Page<VolunteerOffersRequestDto> dtos){

        for (VolunteerOffersRequestDto dto : dtos) {

            Optional<VolunteerAssignmentEntity> optionalAssignment =
                    assignmentRepo.findTopByVolunteerRequest_IdAndStatusInOrderByCreatedAtDesc(
                            dto.getId(),
                            List.of(
                                    AssignmentStatus.ASSIGNED,
                                    AssignmentStatus.IN_PROGRESS,
                                    AssignmentStatus.COMPLETED
                            )
                    );

            if (optionalAssignment.isEmpty()) {
                dto.setReceiverName(null);
                dto.setReceiverId(null);
                dto.setReceiverEmail(null);
                dto.setReceiverMobile(null);
                continue;
            }

            VolunteerAssignmentEntity entity = optionalAssignment.get();

            dto.setAssignmentStatus(entity.getStatus());
            dto.setReceiverType(entity.getReceiver().getUserType().name());
            dto.setReceiverId(entity.getReceiver().getId());
            dto.setReceiverMobile(entity.getReceiver().getMobileNumber());
            dto.setReceiverEmail(entity.getReceiver().getEmail());

            if (entity.getReceiver().getUserType() == UserType.NGO) {
                NgoProfile ngoProfile = ngoProfileService
                        .getProfileByUserId(dto.getReceiverId())
                        .orElseThrow(() ->
                                new RuntimeException("NGO profile missing for userId " + dto.getReceiverId())
                        );

                dto.setReceiverName(ngoProfile.getNgoName());
                dto.setReceiverCity(ngoProfile.getCity());
            } else {
                dto.setReceiverName(entity.getReceiver().getFullName());
            }
        }
    }
}
