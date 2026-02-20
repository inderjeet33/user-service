package com.prerana.userservice.controller;

import com.prerana.userservice.dto.CsrDashboardStatsDto;
import com.prerana.userservice.service.CSRDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasAuthority('TYPE_CSR')")
// CSR is INDIVIDUAL type (as per your decision)
public class CsrDashboardController {

    @Autowired
    private CSRDashboardService csrDashboardService;

    @GetMapping("/csr-stats")
    public ResponseEntity<CsrDashboardStatsDto> stats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(csrDashboardService.getStats(userId));
    }
}
