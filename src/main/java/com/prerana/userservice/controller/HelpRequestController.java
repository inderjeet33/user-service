package com.prerana.userservice.controller;

import com.prerana.userservice.dto.AssignedHelpRequestDto;
import com.prerana.userservice.dto.HelpRequestCreateDto;
import com.prerana.userservice.dto.HelpRequestHistoryDto;
import com.prerana.userservice.dto.HelpRequestResponseDto;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.service.HelpRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/help-requests")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TYPE_INDIVIDUAL')")
public class HelpRequestController {

    private final HelpRequestService helpRequestService;

    @PostMapping("/create")
    public ResponseEntity<HelpRequestResponseDto> createHelpRequest(
            @RequestBody HelpRequestCreateDto dto,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(
                helpRequestService.createHelpRequest(userId, dto)
        );
    }

    @GetMapping("/individual/assigned-help-requests")
    @PreAuthorize("hasAuthority('TYPE_INDIVIDUAL')")
    public ResponseEntity<List<AssignedHelpRequestDto>> individualAssignedHelpRequests(
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(
                helpRequestService.getAssignedHelpRequestsForHelper(userId)
        );
    }


    @PostMapping("/assigned-help-requests/{assignmentId}/update-status")
    public ResponseEntity<?> updateAssignedHelpRequestStatus(
            @PathVariable Long assignmentId,
            @RequestParam AssignmentStatus newStatus,
            HttpServletRequest request
    ) {
        Long helperId = (Long) request.getAttribute("userId");
        helpRequestService.updateHelpAssignmentStatus(helperId, assignmentId, newStatus);
        return ResponseEntity.ok("Status updated");
    }


    @GetMapping("/my")
    public ResponseEntity<List<HelpRequestHistoryDto>> myHelpRequests(
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(helpRequestService.getMyHelpRequests(userId));
    }

}
