package com.prerana.userservice.service;

import com.prerana.userservice.store.TempUserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    @Autowired
    private TempUserStore tempStore;

    public void generateAndSendOtp(String mobileNumber) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // For development, use static OTP for easier testing
        otp = "123456";

        tempStore.storeOtp(mobileNumber, otp);

        // Later: Integrate SMS
        System.out.println("Generated OTP for " + mobileNumber + " = " + otp);
    }

    public boolean verifyOtp(String mobileNumber, String otp) {
        return tempStore.verifyOtp(mobileNumber, otp);
    }

    public boolean isMobileVerified(String mobileNumber) {
        return tempStore.isMobileVerified(mobileNumber);
    }
}
