package com.prerana.userservice.controller;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.VolunteerAssignmentEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.CampaignStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.GalleryStatus;
import com.prerana.userservice.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    private CSRProfileService csrService;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private DonationOfferService donationOfferService;

    @PostMapping("/assign")
    public ResponseEntity<?> assignNgo(@RequestBody ModeratorAssignmentRequestDTO req, HttpServletRequest request) {
        //change it too
        Long moderatorId = (Long) request.getAttribute("userId");
        ModeratorAssignmentEntity assignment = assignmentService.assignNgo(moderatorId, req);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/assign/volunteers")
    public ResponseEntity<?> assignNgo(@RequestBody VolunteerAssignmentRequestDTO req, HttpServletRequest request) {
        //change it too
        Long moderatorId = (Long) request.getAttribute("userId");
        VolunteerAssignmentEntity assignment = volunteerService.assignNgoForVolunteer(moderatorId, req);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/csr/profiles")
    public ResponseEntity<List<CsrProfileDto>> list() {
        return ResponseEntity.ok(csrService.getAllForModerator());
    }

    @PostMapping("/csr/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        csrService.approve(id);
        return ResponseEntity.ok("CSR approved");
    }

    @PostMapping("/csr/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        csrService.reject(id, reason);
        return ResponseEntity.ok("CSR rejected");
    }

    @GetMapping("/volunteer/offers")
    public ResponseEntity<Page<VolunteerOffersRequestDto>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {

        Page<VolunteerOffersRequestDto> result =
                volunteerService.search(page, size, search, type, status);

        return ResponseEntity.ok(result);
    }



    @PostMapping("/assignment/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam AssignmentStatus status) {
        ModeratorAssignmentEntity updated = assignmentService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/moderator/ngos")
    public ResponseEntity<Page<NgoProfile>> searchNgos(@RequestParam(required = false) String city, @RequestParam(required = false) String state, @RequestParam(required = false) String category, @RequestParam(required = false) Boolean verified, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<NgoProfile> result = ngoProfileService.search(city, state, category, verified, page, size);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/moderator/offers")
    public ResponseEntity<Page<DonationOffersRequestDto>> listDonationOffers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,

                                                                             @RequestParam(required = false) String search, @RequestParam(required = false) String category, @RequestParam(required = false) String type, @RequestParam(required = false) DonationOfferStatus status) {
        Page<DonationOffersRequestDto> result = donationOfferService.search(page, size, search, category, type, status);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/moderator/assignments/history")
    public ResponseEntity<Page<ModeratorAssignmentDto>> assignmentHistory(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) AssignmentStatus status) {
        Page<ModeratorAssignmentDto> moderatorassignmentPage = assignmentService.listPaginatedWithFilter(page, size, status);

        return ResponseEntity.ok(moderatorassignmentPage);
    }

    @GetMapping("/donations/{id}/assignment-history")
    public List<AssignmentHistoryDto> getAssignmentHistory(@PathVariable Long id) {
        return assignmentService.getAssignmentHistoryForDoantionId(id);
    }

    // 1. View pending images
    @GetMapping("/gallery/pending/{ngoId}")
    public List<GalleryImageDto> getPendingImages(@PathVariable Long ngoId) {

        return galleryService.findByStatusAndNgoId(ngoId, GalleryStatus.PENDING);
//        return galleryRepo.findByStatus(ImageStatus.PENDING);
    }

    // 🔍 Fetch campaigns for review
    @GetMapping("/campaigns")
    public List<CampaignResponseDto> getCampaignsForReview(
            @RequestParam CampaignStatus status
    ) {
        return campaignService.getCampaignsByStatus(status);
    }

    @PatchMapping("/campaigns/{id}/approve")
    public ResponseEntity<?> approveCampaign(@PathVariable Long id) {
        campaignService.approveCampaign(id);
        return ResponseEntity.ok().build();
    }

    // ❌ Reject campaign
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectCampaign(
            @PathVariable Long id,
            @RequestBody RejectRequest request
    ) {
        campaignService.rejectCampaign(id, request.getReason());
        return ResponseEntity.ok().build();
    }

    // 2. Approve image
    @PostMapping("/gallery/{id}/approve")
    public ResponseEntity<?> approveImage(@PathVariable Long id) {
        galleryService.approveImage(id);//findById(id).orElseThrow(() -> new RuntimeException("Image not found"));
        return ResponseEntity.ok("Image approved");
    }

    // 3. Reject image
    @PostMapping("/gallery/{id}/reject")
    public ResponseEntity<?> rejectImage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        galleryService.rejectImage(id, body);

        return ResponseEntity.ok("Image rejected");
    }

    @PutMapping("/moderator/ngos/{ngoId}/verify")
    public ResponseEntity<Void> verifyNgo(@PathVariable Long ngoId) {
        ngoProfileService.verifyNgo(ngoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/moderator/ngos/{ngoId}/reject")
    public ResponseEntity<Void> rejectNgo(@PathVariable Long ngoId, @RequestBody RejectNgoRequest request) {
        ngoProfileService.rejectNgo(ngoId, request.getReason());
        return ResponseEntity.ok().build();
    }

}
