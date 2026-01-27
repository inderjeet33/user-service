package com.prerana.userservice.controller;

import com.prerana.userservice.dto.VolunteerRequestDto;
import com.prerana.userservice.entity.VolunteerRequestEntity;
import com.prerana.userservice.service.VolunteerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteer")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TYPE_INDIVIDUAL')")
public class VolunteerController {

    private final VolunteerService volunteerService;

    @PostMapping("/create")
    public VolunteerRequestEntity createVolunteerRequest(@RequestBody VolunteerRequestDto dto,
                                                         HttpServletRequest request
    ) {
    Long userId = (Long)request.getAttribute("userId");
        return volunteerService.createVolunteerRequest(userId, dto);
    }

    @GetMapping("/my-requests")
    public List<VolunteerRequestEntity> getMyVolunteerHistory(
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return volunteerService.getMyRequests(userId);
    }

    @PutMapping("/{id}/cancel")
    public void cancelVolunteerRequest(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        volunteerService.cancelRequest(id, userId);
    }

}
