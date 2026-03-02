//package com.prerana.userservice.controller;
//
//import com.prerana.userservice.enums.DonationOfferStatus;
//import com.prerana.userservice.service.DonationOfferService;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/ngo/donations")
//@PreAuthorize("hasAuthority('TYPE_NGO')")
//@RequiredArgsConstructor
//public class NgoDonationController {
//
//    @Autowired
//    private DonationOfferService donationOfferService;
//
//    @PutMapping("/{offerId}/update-progress")
//    public ResponseEntity<String> updateDonationProgress(
//            @PathVariable Long offerId,
//            @RequestParam DonationOfferStatus newStatus,
//            HttpServletRequest request) {
//
//        Long ngoId = (Long) request.getAttribute("userId");
//
//        String response = donationOfferService
//                .updateDonationProgress(ngoId, offerId, newStatus);
//
//        return ResponseEntity.ok(response);
//    }
//}