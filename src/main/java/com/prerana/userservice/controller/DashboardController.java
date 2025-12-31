package com.prerana.userservice.controller;

import com.prerana.userservice.dto.IndividualDashboardStatsDto;
import com.prerana.userservice.dto.ModeratorDashboardStatsDto;
import com.prerana.userservice.dto.NgoDashboardStatsDto;
import com.prerana.userservice.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {


    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/individual-stats")
    @PreAuthorize("hasAuthority('TYPE_INDIVIDUAL')")
    public ResponseEntity<IndividualDashboardStatsDto> getDashboardStats(
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(dashboardService.getIndividualStats(userId));
    }

    @GetMapping("/ngo-stats")
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    public NgoDashboardStatsDto getNgoStats(HttpServletRequest authentication) {

        Long ngoId = (Long) authentication.getAttribute("userId");

        return dashboardService.getNgoStats(ngoId);
    }


    @PreAuthorize("hasAuthority('TYPE_MODERATOR')")
    @GetMapping("/moderator-stats")
    public ModeratorDashboardStatsDto getDashboardStats() {
        return dashboardService.getStats();
    }

}
