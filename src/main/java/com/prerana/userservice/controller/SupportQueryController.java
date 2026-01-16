package com.prerana.userservice.controller;

import com.prerana.userservice.dto.SupportQueryRequestDto;
import com.prerana.userservice.service.SupportQueryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportQueryController {

    private final SupportQueryService service;

    @PostMapping("/query")
    public ResponseEntity<?> submitQuery(
            @RequestBody SupportQueryRequestDto dto,
           HttpServletRequest request
    ) {
        String userType = (String) request.getAttribute("userType");
        Long userId = (Long) request.getAttribute("userId");
        service.submitQuery(dto, userType,userId);
        return ResponseEntity.ok("Query submitted successfully");
    }
}
