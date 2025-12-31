package com.prerana.userservice.controller;

import com.prerana.userservice.dto.*;
import com.prerana.userservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ---------------- SEND OTP ----------------
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody @Valid SendOtpRequest request) {
        authService.sendOtp(request.getMobileNumber());
        return ResponseEntity.ok("OTP sent successfully");
    }

    // ---------------- VERIFY OTP ----------------
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequestDto request) {
        boolean valid = authService.verifyOtp(request);
        if (valid) {
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
    }

    // ---------------- SIGN UP ----------------
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequestDto request) {
        authService.signup(request);
        return ResponseEntity.ok("Sign up successful");
    }

    // ---------------- SET PASSWORD (OPTIONAL FLOW) ----------------
    @PostMapping("/set-password")
    public ResponseEntity<String> setPassword(@RequestBody SetPasswordRequestDto req) {
         authService.setPassword(req);
         return ResponseEntity.ok("Password stored successfully");
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto req) {
        return authService.login(req.getMobileNumber(),req.getPassword(),req.getUserType());
    }
}
