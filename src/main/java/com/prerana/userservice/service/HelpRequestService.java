package com.prerana.userservice.service;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.HelpRequestAssignmentEntity;
import com.prerana.userservice.entity.HelpRequestEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
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
    private HelpRequestRepository helpRequestRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private HelpRequestAssignmentRepository helpRequestAssignmentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    public Page<ModeratorHelpRequestDto> getAllHelpRequests(int page, int size) {
        Sort sort = Sort.by("priority").descending()
                .and(Sort.by("createdAt").descending());
        Pageable pageable = PageRequest.of(page, size, sort);

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

        dto.setPriority(hr.getPriority().name());

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

        if(helper.getUserType().equals(UserType.CSR)) {
            subscriptionService.validateCsrHelpLimit(helper.getId());
        }
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

        notificationService.notify(
                helper,
                "Help Request Assigned",
                "You have been assigned a help request. Please review it."
        );

        notificationService.notify(
                hr.getUser(),
                "Help Assigned",
                "Your help request has been assigned to a helper."
        );

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
    public void updateHelpRequestAssignmentStatus(
            Long helperId,
            Long assignmentId,
            AssignmentStatus newStatus
    ) {

        HelpRequestAssignmentEntity assignment =
                helpRequestAssignmentRepository.findById(assignmentId)
                        .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getHelper().getId().equals(helperId)) {
            throw new RuntimeException("Unauthorized");
        }

        assignment.setStatus(newStatus);
        helpRequestAssignmentRepository.save(assignment);
    }

//    @Transactional
//    public void updateHelpAssignmentStatus(
//            Long helperId,
//            Long assignmentId,
//            AssignmentStatus newStatus
//    ) {
//        HelpRequestAssignmentEntity assignment =
//                helpRequestAssignmentRepository.findById(assignmentId)
//                        .orElseThrow(() -> new RuntimeException("Assignment not found"));
//
//        if (!assignment.getHelper().getId().equals(helperId)) {
//            throw new RuntimeException("Not authorized");
//        }
//
//        assignment.setStatus(newStatus);
//
//        HelpRequestEntity hr = assignment.getHelpRequest();
//        hr.setStatus(mapToHelpRequestStatus(newStatus));
//
//        helpRequestRepository.save(hr);
//        helpRequestAssignmentRepository.save(assignment);
//    }

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

        if (!assignment.isActive()) {
            throw new RuntimeException("Cannot update historical assignment");
        }
        HelpRequestEntity hr = assignment.getHelpRequest();

        // START WORK
        if (newStatus == AssignmentStatus.IN_PROGRESS &&
                assignment.getStatus() == AssignmentStatus.ASSIGNED) {

            assignment.setStatus(AssignmentStatus.IN_PROGRESS);
            hr.setStatus(HelpRequestStatus.IN_PROGRESS);
        } else if (newStatus == AssignmentStatus.HELP_REJECTED_BY_HELPER &&
                (assignment.getStatus() == AssignmentStatus.ASSIGNED || assignment.getStatus() == AssignmentStatus.IN_PROGRESS)) {

            assignment.setStatus(AssignmentStatus.HELP_REJECTED_BY_HELPER);
            hr.setStatus(HelpRequestStatus.OPEN);
        }
        // MARK DELIVERED
        else if (newStatus == AssignmentStatus.COMPLETED &&
                assignment.getStatus() == AssignmentStatus.IN_PROGRESS) {

            assignment.setStatus(AssignmentStatus.COMPLETED);
            hr.setStatus(HelpRequestStatus.DELIVERED);
        } else {
            throw new RuntimeException("Invalid status transition");
        }



        helpRequestRepository.save(hr);
        helpRequestAssignmentRepository.save(assignment);
    }



//    public List<AssignedHelpRequestDto> getAssignedHelpRequestsForHelper(Long helperId) {
//
//        List<HelpRequestAssignmentEntity> assignments =
//                helpRequestAssignmentRepository.findActiveAssignmentsForHelper(helperId);
//
//        return assignments.stream().map(a -> {
//            HelpRequestEntity hr = a.getHelpRequest();
//            UserEntity requester = hr.getUser();
//
//            AssignedHelpRequestDto dto = new AssignedHelpRequestDto();
//
//            // Assignment
//            dto.setAssignmentId(a.getId());
//            dto.setAssignmentStatus(a.getStatus());
//            dto.setAssignedAt(a.getCreatedAt());
//
//            // Help request
//            dto.setHelpRequestId(hr.getId());
//            dto.setDonationCategory(hr.getDonationCategory().name());
//            dto.setHelpType(hr.getHelpType().name());
//            dto.setAmount(hr.getAmount());
//            dto.setItemDetails(hr.getItemDetails());
//            dto.setQuantity(hr.getQuantity());
//            dto.setUrgency(hr.getUrgency());
//            dto.setLocation(hr.getLocation());
//            dto.setPreferredContact(hr.getPreferredContact());
//            dto.setReason(hr.getReason());
//            dto.setHelpRequestStatus(hr.getStatus());
//
//            // Requester
//            dto.setRequesterId(requester.getId());
//            dto.setRequesterName(requester.getFullName());
//            dto.setRequesterMobile(requester.getMobileNumber());
//
//            return dto;
//        }).toList();
//    }

    public List<AssignedHelpRequestDto> getAssignedHelpRequestsForHelper(Long helperId, String view) {

        List<AssignmentStatus> statuses;

        if ("HISTORY".equalsIgnoreCase(view)) {
            statuses = List.of(
                    AssignmentStatus.COMPLETED,
                    AssignmentStatus.HELP_REJECTED_BY_HELPER,
                    AssignmentStatus.HELP_CANCELLED_BY_RECEIVER
            );
        } else {
            statuses = List.of(
                    AssignmentStatus.ASSIGNED,
                    AssignmentStatus.IN_PROGRESS
            );
        }

        List<HelpRequestAssignmentEntity> assignments =
                helpRequestAssignmentRepository
                        .findByHelper_IdAndStatusInOrderByCreatedAtDesc(helperId, statuses);

        return assignments.stream().map(a -> {
            HelpRequestEntity hr = a.getHelpRequest();
            UserEntity requester = hr.getUser();

            AssignedHelpRequestDto dto = new AssignedHelpRequestDto();

            dto.setAssignmentId(a.getId());
            dto.setAssignmentStatus(a.getStatus());
            dto.setAssignedAt(a.getCreatedAt());

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

            dto.setRequesterId(requester.getId());
            dto.setRequesterName(requester.getFullName());
            dto.setRequesterMobile(requester.getMobileNumber());

            return dto;
        }).toList();
    }

    @Autowired
    private SubscriptionGuardService subscriptionGuardService;

    @Transactional
    public HelpRequestResponseDto createHelpRequest(Long userId, HelpRequestCreateDto dto) {


        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        subscriptionGuardService.checkHelpRequestAllowed(userId);

        boolean isPriorityUser =
                subscriptionService.hasFeature(userId, FeatureKey.PRIORITY_ASSIGNMENT);



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

        entity.setPriority(
                isPriorityUser ? PriorityLevel.PRIORITY : PriorityLevel.NORMAL
        );
        entity = helpRequestRepository.save(entity);

        return toDto(entity);
    }

//    public List<HelpRequestHistoryDto> getMyHelpRequests(Long userId) {
//
//        return helpRequestRepository
//                .findByUser_IdOrderByCreatedAtDesc(userId)
//                .stream()
//                .map(this::mapToHistoryDto)
//
//                .collect(Collectors.toList());
//    }

    public List<HelpRequestHistoryDto> getMyHelpRequests(Long userId, String view) {

        List<HelpRequestStatus> statuses;

        if ("HISTORY".equalsIgnoreCase(view)) {
            statuses = List.of(
                    HelpRequestStatus.COMPLETED,
                    HelpRequestStatus.CANCELLED,
                    HelpRequestStatus.REJECTED
            );
        } else { // ACTIVE default
            statuses = List.of(
                    HelpRequestStatus.OPEN,
                    HelpRequestStatus.APPROVED,
                    HelpRequestStatus.ASSIGNED,
                    HelpRequestStatus.IN_PROGRESS,
                    HelpRequestStatus.DELIVERED
            );
        }

        return helpRequestRepository
                .findByUser_IdAndStatusInOrderByCreatedAtDesc(userId, statuses)
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

        Optional<HelpRequestAssignmentEntity> assignmentOpt =
                helpRequestAssignmentRepository
                        .findTopByHelpRequest_IdOrderByCreatedAtDesc(e.getId());

        if (assignmentOpt.isPresent()) {
            HelpRequestAssignmentEntity assignment = assignmentOpt.get();
            UserEntity helper = assignment.getHelper();

            dto.setReceiverName(helper.getFullName());
            dto.setReceiverMobile(helper.getMobileNumber());
            dto.setReceiverEmail(helper.getEmail());
        }

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

    public List<HelperDto> listHelpersOnUserType(UserType usertype) {
        return userRepository.findHelpersByPriority(usertype)
                .stream().map(u -> new HelperDto(
                        u.getId(),
                        u.getFullName(),
                        u.getUserType())).toList();
    }

    @Transactional
    public void confirmHelpReceived(Long userId, Long helpRequestId) {

        HelpRequestEntity hr = helpRequestRepository.findById(helpRequestId)
                .orElseThrow(() -> new RuntimeException("Help request not found"));

        if (!hr.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        if (hr.getStatus() != HelpRequestStatus.DELIVERED) {
            throw new RuntimeException("Help not yet delivered");
        }

        hr.setStatus(HelpRequestStatus.COMPLETED);
        helpRequestRepository.save(hr);

        HelpRequestAssignmentEntity assignment =
                helpRequestAssignmentRepository.findTopByHelpRequest_IdAndStatusInOrderByCreatedAtDesc(
                        hr.getId(),
                        List.of(AssignmentStatus.COMPLETED)
                ).orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(AssignmentStatus.COMPLETED);
        helpRequestAssignmentRepository.save(assignment);

//        completeAssignment(offer.getAmount(), assignment);
    }

    @Transactional
    public void cancelHelpRequest(Long userId, Long helpRequestId) {

        HelpRequestEntity hr = helpRequestRepository.findById(helpRequestId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!hr.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        if (!(hr.getStatus() == HelpRequestStatus.OPEN ||
                hr.getStatus() == HelpRequestStatus.APPROVED || hr.getStatus() == HelpRequestStatus.ASSIGNED)) {
            throw new RuntimeException("Cannot cancel at this stage");
        }

        hr.setStatus(HelpRequestStatus.CANCELLED);
        helpRequestRepository.save(hr);

        Optional<HelpRequestAssignmentEntity> optionalAssignment =
                helpRequestAssignmentRepository.findTopByHelpRequest_IdAndStatusInOrderByCreatedAtDesc(
                        hr.getId(),
                        List.of(AssignmentStatus.ASSIGNED,AssignmentStatus.IN_PROGRESS)
                );

        if (optionalAssignment.isPresent()) {
            HelpRequestAssignmentEntity assignment = optionalAssignment.get();
            assignment.setStatus(AssignmentStatus.HELP_CANCELLED_BY_RECEIVER);
            helpRequestAssignmentRepository.save(assignment);
        }
    }

}
