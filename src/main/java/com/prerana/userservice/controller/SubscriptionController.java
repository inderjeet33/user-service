package com.prerana.userservice.controller;


import com.prerana.userservice.dto.*;
import com.prerana.userservice.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /* ---------------------------------
       Get current user's subscription
       --------------------------------- */
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('TYPE_INDIVIDUAL','TYPE_CSR','TYPE_NGO')")
    public ResponseEntity<UserSubscriptionDto> mySubscription(
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(subscriptionService.getMySubscription(userId));
    }

    /* ---------------------------------
       List all available plans
       --------------------------------- */
    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanDto>> getPlans(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(subscriptionService.getAllPlans(userId));
    }

    /* ---------------------------------
       Upgrade / Change plan
       --------------------------------- */
    @PostMapping("/upgrade")
    @PreAuthorize("hasAnyAuthority('TYPE_INDIVIDUAL','TYPE_CSR','TYPE_NGO')")
    public ResponseEntity<?> upgrade(
            @RequestParam String planCode,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        subscriptionService.upgradePlan(userId, planCode);
        return ResponseEntity.ok("Subscription upgraded successfully");
    }
}

