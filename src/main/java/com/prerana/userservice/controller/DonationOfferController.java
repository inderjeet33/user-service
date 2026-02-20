package com.prerana.userservice.controller;

import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.dto.NgoPublicDto;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.service.CertificateService;
import com.prerana.userservice.service.DonationOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/donations")
@PreAuthorize("hasAnyAuthority('TYPE_INDIVIDUAL', 'TYPE_CSR')")
@RequiredArgsConstructor
public class DonationOfferController {

    @Autowired
    private final DonationOfferService donationOfferService;

    @Autowired
    private CertificateService certificateService;

    @PostMapping("/create")
    public ResponseEntity<DonationOffersRequestDto> createDonationOffer(
            @RequestBody DonationOffersRequestDto dto,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(donationOfferService.createDonationOffer(userId, dto));
    }

    @GetMapping("/my-offers")
    public ResponseEntity<List<DonationOffersRequestDto>> getMyOffers(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<DonationOffersRequestDto> offers = donationOfferService.getOffersByUser(userId);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/{offerId}/certificate")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long offerId,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        byte[] pdf = certificateService.generateCertificate(offerId, userId);

        // Pseudo check
//        if (!assignment.getDonor().getId().equals(currentUserId)) {
//            throw new AccessDeniedException("Not your certificate");
//        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=donation-certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    @GetMapping("/{id}/receiver")
    public ResponseEntity<NgoPublicDto> getReceiverForDonation(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(donationOfferService.getReceiverForDonation(id, userId));
    }

    @PutMapping("/{offerId}/cancel")
    public ResponseEntity<Void> cancelDonationOffer(
            @PathVariable Long offerId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        donationOfferService.cancelDonationOffer(offerId, userId);
        return ResponseEntity.ok().build();
    }

}
