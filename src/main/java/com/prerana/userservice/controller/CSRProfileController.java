package com.prerana.userservice.controller;

import com.prerana.userservice.dto.CSRProfileRequestDto;
import com.prerana.userservice.dto.CSRProfileResponseDto;
import com.prerana.userservice.service.CSRProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/csr/profile")
@PreAuthorize("hasAuthority('TYPE_CSR')")
@RequiredArgsConstructor
public class CSRProfileController {

    private final CSRProfileService service;

    @PostMapping("/complete")
    public ResponseEntity<?> completeProfile(
            HttpServletRequest request,
            @RequestBody CSRProfileRequestDto dto
    ) {
        Long userId = (Long) request.getAttribute("userId");
        service.completeProfile(userId, dto);
        return ResponseEntity.ok("CSR profile submitted for verification");
    }

    @GetMapping
    public ResponseEntity<CSRProfileResponseDto> getProfile(
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(service.getProfile(userId));
    }
}
