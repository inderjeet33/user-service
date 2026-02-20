package com.prerana.userservice.controller;

import com.prerana.userservice.dto.HelperDto;
import com.prerana.userservice.dto.ModeratorHelpRequestDto;
import com.prerana.userservice.enums.HelpRequestStatus;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.service.HelpRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator/help-requests")
@PreAuthorize("hasAuthority('TYPE_MODERATOR')")
public class ModeratorHelpRequestController {

    @Autowired
    private HelpRequestService service;

    @GetMapping
    public ResponseEntity<Page<ModeratorHelpRequestDto>> list(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(service.getAllHelpRequests(page, size));
    }

    @GetMapping("/helpers")
    public ResponseEntity<List<HelperDto>> listHelpers(@RequestParam UserType userType) {
    List<HelperDto> dtos = service.listHelpersOnUserType(userType);
    return ResponseEntity.ok(dtos);
    }


        @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        service.updateHelpRequestStatus(id, HelpRequestStatus.APPROVED);
        return ResponseEntity.ok("Approved");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        service.updateHelpRequestStatus(id, HelpRequestStatus.REJECTED);
        return ResponseEntity.ok("Rejected");
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assign(
            @PathVariable Long id,
            @RequestParam Long helperId,
            HttpServletRequest request
    ) {
        Long moderatorId = (Long) request.getAttribute("userId");
        service.assignHelper(moderatorId, id, helperId);
        return ResponseEntity.ok("Assigned");
    }
}
