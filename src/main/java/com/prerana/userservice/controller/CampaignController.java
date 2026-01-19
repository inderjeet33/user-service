package com.prerana.userservice.controller;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.service.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    // Create campaign (NGO or future individual)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<CampaignResponseDto> createCampaign(
            @RequestPart("data") CreateCampaignDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ,HttpServletRequest request) {
        Long ownerId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(campaignService.createCampaign(dto,ownerId,image));
    }


    // Get all active campaigns (for donors)
//    @GetMapping
//    public ResponseEntity<List<CampaignResponseDto>> getActiveCampaigns() {
//        return ResponseEntity.ok(campaignService.getAllActiveCampaigns());
//    }

    @GetMapping("/public")
    public ResponseEntity<List<CampaignPublicDto>> getPublicCampaigns() {
        return ResponseEntity.ok(campaignService.getPublicCampaigns());
    }
    // Get campaigns by owner (for NGO dashboard)
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<List<CampaignResponseDto>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(campaignService.getCampaignsByOwner(ownerId));
    }

    @PatchMapping("/{id}/withdraw")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<?> withdrawCampaign(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");


        try {
            campaignService.withdrawCampaign(id, userId);
        }catch(Exception e){
            System.out.println("exception "+e);
        }

        return ResponseEntity.ok("Campaign withdrawn successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<CampaignResponseDto> updateCampaign(
            @PathVariable Long id,
            @RequestBody UpdateCampaignDto dto,
            HttpServletRequest request
    ) {
        Long ownerId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(campaignService.updateCampaign(id, ownerId, dto));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<Void> markCampaignCompleted(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long ownerId = (Long) request.getAttribute("userId");
        campaignService.markAsCompleted(id, ownerId);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @GetMapping("/my-campaigns")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<List<CampaignResponseDto>> getMyCampaigns(HttpServletRequest request) {
        Long ownerId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(campaignService.getCampaignsByOwner(ownerId));
    }

    @PostMapping("/{id}/updates")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<Void> addUpdate(
            @PathVariable Long id,
            @RequestBody CreateCampaignUpdateDto dto,
            HttpServletRequest request
    ) {
        Long ownerId = (Long) request.getAttribute("userId");
        campaignService.addUpdate(id, ownerId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/updates")
    public ResponseEntity<List<CampaignUpdateResponseDto>> getUpdates(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(campaignService.getUpdates(id));
    }



}
