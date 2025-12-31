package com.prerana.userservice.controller;

import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.dto.UserResponseDto;
import com.prerana.userservice.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/me")
    public UserResponseDto getLoggedInUser(HttpServletRequest request) {

            Long userId = (Long) request.getAttribute("userId");

        return userService.getUserDetails(userId);
    }

    @PutMapping("/profile/complete")
    public ResponseEntity<?> completeProfile(
            @RequestBody Map<String, String> req,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        userService.completeIndividualProfile(userId, req);
        return ResponseEntity.ok().build();
    }

}
