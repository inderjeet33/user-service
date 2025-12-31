package com.prerana.userservice.controller;

import com.prerana.userservice.service.interfaces.DonationExportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exports")
@RequiredArgsConstructor
public class ExportController {

    @Autowired
    private final DonationExportService donationExportService;

    @GetMapping("/donations/my")
    @PreAuthorize("hasAuthority('TYPE_INDIVIDUAL')")
    public ResponseEntity<byte[]> exportMyDonations(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        byte[] excel = donationExportService.exportMyDonationHistory(userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=donation-history.xlsx")
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        ))
                .body(excel);
    }

    @GetMapping("/donations/ngo")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public ResponseEntity<byte[]> exportNgoDonations(HttpServletRequest request) {

        Long ngoId = (Long) request.getAttribute("userId");

        byte[] excel = donationExportService.generateNgoDonationsExcel(ngoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Prerana-NGO-Donations.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/moderator/ngos")
    @PreAuthorize("hasAuthority('TYPE_MODERATOR')")
    public ResponseEntity<byte[]> exportModeratorNgos() {

        byte[] excel = donationExportService.exportModeratorNgoList();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Prerana-NGO-List.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/moderator/donations")
    @PreAuthorize("hasAuthority('TYPE_MODERATOR')")
    public ResponseEntity<byte[]> exportModeratorDonations(HttpServletRequest request) {

        Long moderatorId = (Long) request.getAttribute("userId");

        byte[] excel = donationExportService.exportModeratorDonations(moderatorId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Prerana-Moderator-Donations.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/moderator/assignments")
    @PreAuthorize("hasAuthority('TYPE_MODERATOR')")
    public ResponseEntity<byte[]> downloadAssignmentHistoryExcel(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        byte[] excel = donationExportService.exportModeratorAssignmentExcel(userId);
//                ExcelExportUtil.exportModeratorAssignmentHistory(assignments);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Prerana-Assignment-History.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }

}
