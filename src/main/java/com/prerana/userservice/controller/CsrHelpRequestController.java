package com.prerana.userservice.controller;

import com.prerana.userservice.dto.AssignedHelpRequestDto;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.service.HelpRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/csr/help-requests")
@PreAuthorize("hasAuthority('TYPE_CSR')")
public class CsrHelpRequestController {

    private final HelpRequestService service;

    public CsrHelpRequestController(HelpRequestService service) {
        this.service = service;
    }

    @GetMapping("/assigned")
    public List<AssignedHelpRequestDto> assigned(HttpServletRequest request) {
        Long csrId = (Long) request.getAttribute("userId");
        return service.getAssignedHelpRequestsForHelper(csrId);
    }

    @PostMapping("/{assignmentId}/update-status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long assignmentId,
            @RequestParam AssignmentStatus status,
            HttpServletRequest request
    ) {
        Long csrId = (Long) request.getAttribute("userId");
        service.updateHelpRequestAssignmentStatus(csrId, assignmentId, status);
        return ResponseEntity.ok("Updated");
    }
}
