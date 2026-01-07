package com.prerana.userservice.controller;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.service.DonationOfferService;
import com.prerana.userservice.service.ModeratorAssignmentService;
import com.prerana.userservice.service.NGOProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/moderator")
@PreAuthorize("hasAuthority('TYPE_MODERATOR')")
@RequiredArgsConstructor
public class ModeratorController {

    @Autowired
    private final ModeratorAssignmentService assignmentService;

    @Autowired
    private NGOProfileService ngoProfileService;


    @Autowired
    private DonationOfferService donationOfferService;

    @PostMapping("/assign")
    public ResponseEntity<?> assignNgo(
            @RequestBody ModeratorAssignmentRequestDTO req,
            HttpServletRequest request
    ) {
        Long moderatorId = (Long) request.getAttribute("userId");
        ModeratorAssignmentEntity assignment = assignmentService.assignNgo(moderatorId, req);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/assignment/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam AssignmentStatus status
    ) {
        ModeratorAssignmentEntity updated = assignmentService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/moderator/ngos")
    public ResponseEntity<Page<NgoProfile>> searchNgos(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<NgoProfile> result =
                ngoProfileService.search(city, state, category,verified, page, size);
        return ResponseEntity.ok(result);
    }



    @GetMapping("/moderator/offers")
    public ResponseEntity<Page<DonationOffersRequestDto>> listDonationOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) DonationOfferStatus status
    ) {
        Page<DonationOffersRequestDto> result =
                donationOfferService.search(page, size, search, category, type, status);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/moderator/assignments/history")
    public ResponseEntity<Page<ModeratorAssignmentDto>> assignmentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) AssignmentStatus status
    ) {
        Page<ModeratorAssignmentDto> moderatorassignmentPage =
                assignmentService.listPaginatedWithFilter(page, size, status);

        return ResponseEntity.ok(moderatorassignmentPage);
    }
    @GetMapping("/donations/{id}/assignment-history")
    public List<AssignmentHistoryDto> getAssignmentHistory(
            @PathVariable Long id
    ) {
        return assignmentService.getAssignmentHistoryForDoantionId(id);
    }

    @PutMapping("/moderator/ngos/{ngoId}/verify")
    public ResponseEntity<Void> verifyNgo(@PathVariable Long ngoId) {
        ngoProfileService.verifyNgo(ngoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/moderator/ngos/{ngoId}/reject")
    public ResponseEntity<Void> rejectNgo(@PathVariable Long ngoId) {
        ngoProfileService.rejectNgo(ngoId);
        return ResponseEntity.ok().build();
    }

}
