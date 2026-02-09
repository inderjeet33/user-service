package com.prerana.userservice.service;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.HelpRequestAssignmentEntity;
import com.prerana.userservice.entity.HelpRequestEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.*;
import com.prerana.userservice.repository.HelpRequestAssignmentRepository;
import com.prerana.userservice.repository.HelpRequestRepository;
import com.prerana.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    @Autowired
    private final HelpRequestRepository helpRequestRepository;
    @Autowired
    private final HelpRequestAssignmentRepository helpRequestAssignmentRepository;
    @Autowired
    private final UserRepository userRepository;

    public Page<ModeratorHelpRequestDto> getAllHelpRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return helpRequestRepository.findAll(pageable)
                .map(this::mapToModeratorDto);
    }

    private ModeratorHelpRequestDto mapToModeratorDto(HelpRequestEntity hr) {

        ModeratorHelpRequestDto dto = new ModeratorHelpRequestDto();

        // -----------------------------
        // Help Request Info
        // -----------------------------
        dto.setId(hr.getId());
        dto.setDonationCategory(hr.getDonationCategory().name());
        dto.setHelpType(hr.getHelpType().name());
        dto.setUrgency(hr.getUrgency());
        dto.setLocation(hr.getLocation());
        dto.setReason(hr.getReason());
        dto.setStatus(hr.getStatus());
        dto.setCreatedAt(hr.getCreatedAt());

        // -----------------------------
        // Requester (Individual)
        // -----------------------------
        UserEntity requester = hr.getUser();
        dto.setRequesterId(requester.getId());
        dto.setRequesterName(requester.getFullName());
        dto.setRequesterMobile(requester.getMobileNumber());

        // -----------------------------
        // Assignment (if exists)
        // -----------------------------
        Optional<HelpRequestAssignmentEntity> assignmentOpt =
                helpRequestAssignmentRepository
                        .findTopByHelpRequest_IdOrderByCreatedAtDesc(hr.getId());

        if (assignmentOpt.isPresent()) {
            HelpRequestAssignmentEntity assignment = assignmentOpt.get();

            dto.setAssignmentId(assignment.getId());
            dto.setAssignmentStatus(assignment.getStatus());
            dto.setHelperName(assignment.getHelper().getFullName());
            dto.setHelperType(assignment.getHelperType().name());
        }

        return dto;
    }


    @Transactional
    public void assignHelper(
            Long moderatorId,
            Long helpRequestId,
            Long helperId
    ) {

        HelpRequestEntity hr = helpRequestRepository.findById(helpRequestId)
                .orElseThrow(() -> new RuntimeException("Help request not found"));

        if (hr.getStatus() != HelpRequestStatus.APPROVED) {
            throw new RuntimeException("Help request must be APPROVED before assignment");
        }

        UserEntity helper = userRepository.findById(helperId)
                .orElseThrow(() -> new RuntimeException("Helper not found"));

        UserEntity moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Moderator not found"));

        HelpRequestAssignmentEntity assignment =
                HelpRequestAssignmentEntity.builder()
                        .moderator(moderator)
                        .helpRequest(hr)
                        .helper(helper)
                        .helperType(helper.getUserType())
                        .status(AssignmentStatus.ASSIGNED)
                        .build();

        helpRequestAssignmentRepository.save(assignment);

        hr.setStatus(HelpRequestStatus.ASSIGNED);
        helpRequestRepository.save(hr);
    }

    @Transactional
    public void updateHelpRequestStatus(Long helpRequestId, HelpRequestStatus newStatus) {

        HelpRequestEntity hr = helpRequestRepository.findById(helpRequestId)
                .orElseThrow(() -> new RuntimeException("Help request not found"));

        if (hr.getStatus() != HelpRequestStatus.OPEN) {
            throw new RuntimeException("Only OPEN requests can be updated");
        }

        hr.setStatus(newStatus);
        helpRequestRepository.save(hr);
    }


    @Transactional
    public void updateHelpAssignmentStatus(
            Long helperId,
            Long assignmentId,
            AssignmentStatus newStatus
    ) {
        HelpRequestAssignmentEntity assignment =
                helpRequestAssignmentRepository.findById(assignmentId)
                        .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getHelper().getId().equals(helperId)) {
            throw new RuntimeException("Not authorized");
        }

        assignment.setStatus(newStatus);

        HelpRequestEntity hr = assignment.getHelpRequest();
        hr.setStatus(mapToHelpRequestStatus(newStatus));

        helpRequestRepository.save(hr);
        helpRequestAssignmentRepository.save(assignment);
    }

    private HelpRequestStatus mapToHelpRequestStatus(AssignmentStatus status) {
        return switch (status) {
            case IN_PROGRESS -> HelpRequestStatus.IN_PROGRESS;
            case COMPLETED -> HelpRequestStatus.COMPLETED;
            case CANCELLED_BY_DONOR-> HelpRequestStatus.OPEN;
            default -> HelpRequestStatus.ASSIGNED;
        };
    }


    public List<AssignedHelpRequestDto> getAssignedHelpRequestsForHelper(Long helperId) {

        List<HelpRequestAssignmentEntity> assignments =
                helpRequestAssignmentRepository.findActiveAssignmentsForHelper(helperId);

        return assignments.stream().map(a -> {
            HelpRequestEntity hr = a.getHelpRequest();
            UserEntity requester = hr.getUser();

            AssignedHelpRequestDto dto = new AssignedHelpRequestDto();

            // Assignment
            dto.setAssignmentId(a.getId());
            dto.setAssignmentStatus(a.getStatus());
            dto.setAssignedAt(a.getCreatedAt());

            // Help request
            dto.setHelpRequestId(hr.getId());
            dto.setDonationCategory(hr.getDonationCategory().name());
            dto.setHelpType(hr.getHelpType().name());
            dto.setAmount(hr.getAmount());
            dto.setItemDetails(hr.getItemDetails());
            dto.setQuantity(hr.getQuantity());
            dto.setUrgency(hr.getUrgency());
            dto.setLocation(hr.getLocation());
            dto.setPreferredContact(hr.getPreferredContact());
            dto.setReason(hr.getReason());
            dto.setHelpRequestStatus(hr.getStatus());

            // Requester
            dto.setRequesterId(requester.getId());
            dto.setRequesterName(requester.getFullName());
            dto.setRequesterMobile(requester.getMobileNumber());

            return dto;
        }).toList();
    }


    @Transactional
    public HelpRequestResponseDto createHelpRequest(Long userId, HelpRequestCreateDto dto) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HelpRequestEntity entity = HelpRequestEntity.builder()
                .user(user)
                .donationCategory(DonationCategory.valueOf(dto.getDonationCategory()))
                .helpType(HelpType.valueOf(dto.getHelpType()))
                .amount(dto.getAmount())
                .itemDetails(dto.getItemDetails())
                .quantity(dto.getQuantity())
                .urgency(dto.getUrgency())
                .location(dto.getLocation())
                .preferredContact(dto.getPreferredContact())
                .reason(dto.getReason())
                .status(HelpRequestStatus.OPEN)
                .build();

        entity = helpRequestRepository.save(entity);

        return toDto(entity);
    }

    public List<HelpRequestHistoryDto> getMyHelpRequests(Long userId) {

        return helpRequestRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToHistoryDto)

                .collect(Collectors.toList());
    }

    private HelpRequestHistoryDto mapToHistoryDto(HelpRequestEntity e) {

        HelpRequestHistoryDto dto = new HelpRequestHistoryDto();

        dto.setId(e.getId());
        dto.setDonationCategory(e.getDonationCategory().name());
        dto.setHelpType(e.getHelpType().name());
        dto.setAmount(e.getAmount());
        dto.setItemDetails(e.getItemDetails());
        dto.setQuantity(e.getQuantity());
        dto.setUrgency(e.getUrgency());
        dto.setLocation(e.getLocation());
        dto.setPreferredContact(e.getPreferredContact());
        dto.setReason(e.getReason());
        dto.setStatus(e.getStatus());
        dto.setCreatedAt(e.getCreatedAt());

        return dto;

    }

        private HelpRequestResponseDto toDto(HelpRequestEntity entity) {

        return HelpRequestResponseDto.builder()
                .id(entity.getId())
                .donationCategory(entity.getDonationCategory().name())
                .helpType(entity.getHelpType().name())
                .amount(entity.getAmount())
                .itemDetails(entity.getItemDetails())
                .quantity(entity.getQuantity())
                .urgency(entity.getUrgency())
                .location(entity.getLocation())
                .preferredContact(entity.getPreferredContact())
                .reason(entity.getReason())
                .status(entity.getStatus())

                .userId(entity.getUser().getId())
                .userName(entity.getUser().getFullName())
                .userEmail(entity.getUser().getEmail())
                .userMobile(entity.getUser().getMobileNumber())
                .build();
    }


    public List<HelperDto> listHelpers() {

        List<HelperDto> helpers =
                userRepository.findHelpers().stream()
                        .map(u -> new HelperDto(
                                u.getId(),
                                u.getFullName(),
                                u.getUserType()
                        ))
                        .toList();

        return helpers;
    }
}
