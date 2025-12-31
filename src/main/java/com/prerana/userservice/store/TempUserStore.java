package com.prerana.userservice.store;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TempUserStore {

    // Stores mobile -> OTP
    private final Map<String, String> otpMap = new HashMap<>();

    // Stores mobile -> verification status
    private final Map<String, Boolean> otpVerifiedMap = new HashMap<>();

    // Stores mobile -> encrypted password (after user sets password)
    private final Map<String, String> passwordMap = new HashMap<>();


    // ----------------- OTP Section -----------------

    public void storeOtp(String mobile, String otp) {
        otpMap.put(mobile, otp);
        otpVerifiedMap.put(mobile, false);
    }

    public boolean verifyOtp(String mobile, String otp) {
        if (otpMap.containsKey(mobile) && otpMap.get(mobile).equals(otp)) {
            otpVerifiedMap.put(mobile, true);
            return true;
        }
        return false;
    }

    public boolean isMobileVerified(String mobile) {
        return otpVerifiedMap.getOrDefault(mobile, false);
    }


    // ----------------- Password Section -----------------

    public void storePassword(String mobile, String encryptedPassword) {
        passwordMap.put(mobile, encryptedPassword);
    }

    public boolean hasPassword(String mobile) {
        return passwordMap.containsKey(mobile);
    }

    public String getPassword(String mobile) {
        return passwordMap.get(mobile);
    }


    // ----------------- Clear After Signup -----------------

    public void clear(String mobile) {
        otpMap.remove(mobile);
        otpVerifiedMap.remove(mobile);
        passwordMap.remove(mobile);
    }
}