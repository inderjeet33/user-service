package com.prerana.userservice.controller;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.service.JwtService;
import com.prerana.userservice.service.NGOProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('TYPE_NGO')")
@RequestMapping("/ngo/profile")
public class NGOProfileController {

    @Autowired private NGOProfileService profileService;

    @Autowired
    private JwtService jwtUtil; // for extracting user id from token or use @AuthenticationPrincipal

    // Complete NGO profile (later make it multipart for docs)
    @PostMapping(value = "/complete")
    public ResponseEntity<?> completeProfile(
            @RequestBody @Valid NgoProfileRequestDto profileRequest, HttpServletRequest request
    ) throws Exception {
        Long userId = (Long)request.getAttribute("userId");
        NGOProfileEntity profile = profileService.completeProfile(userId, profileRequest);
        return ResponseEntity.ok(Map.of("message", "Profile completed, pending verification", "profileId", profile.getId()));
    }

    @GetMapping("/me")
    public ResponseEntity<NgoProfile> getMyProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return profileService.getProfileByUserId(userId)
                .map(p -> ResponseEntity.ok(p))
                .orElse(ResponseEntity.ok(new NgoProfile()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NgoProfile> getProfile(@PathVariable Long id) {
        return profileService.getProfileByUserId(id)
                .map(p -> ResponseEntity.ok(p))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/assigned-offers")
    public ResponseEntity<List<AssignedOfferDto>> getAssignedOffers(
            HttpServletRequest request) {

        Long ngoUserId = (Long)request.getAttribute("userId");
        return ResponseEntity.ok(profileService.getAssignedOffers(ngoUserId));
    }

    @GetMapping("/assigned-volunteers")
    public ResponseEntity<List<AssignedVolunteerDto>> getAssignedVolunteers(
            HttpServletRequest request) {

        Long ngoUserId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(
                profileService.getAssignedVolunteers(ngoUserId)
        );
    }
    @PostMapping("/assigned-offers/{assignmentId}/update-status")
    public ResponseEntity<?> updateAssignedOfferStatus(
            @PathVariable Long assignmentId,
            @RequestParam AssignmentStatus newStatus,
            HttpServletRequest request
    ) {
        Long ngoId = (Long)request.getAttribute("userId");
        return ResponseEntity.ok(profileService.updateAssignedOfferStatus(ngoId, assignmentId, newStatus));
    }

    @PostMapping("/assigned-volunteers/{assignmentId}/update-status")
    public ResponseEntity<?> updateAssignedVolunteerStatus(
            @PathVariable Long assignmentId,
            @RequestParam AssignmentStatus newStatus,
            HttpServletRequest request
    ) {
        Long ngoId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(
                profileService.updateAssignedVolunteerStatus(ngoId, assignmentId, newStatus)
        );
    }


}
