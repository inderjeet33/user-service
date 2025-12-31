package com.prerana.userservice.service;

import com.prerana.userservice.dto.SetPasswordRequest;
import com.prerana.userservice.store.TempUserStore;
import com.prerana.userservice.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordSetupService {

    @Autowired
    private  PasswordEncoder encoder;
    @Autowired
    private TempUserStore tempUserStore;

    public void setPassword(SetPasswordRequest req) {
        if (!tempUserStore.isMobileVerified(req.getMobileNumber())) {
            throw new RuntimeException("Mobile number not verified");
        }

        String encPwd = encoder.encode(req.getPassword());

        // store temporarily
        tempUserStore.storePassword(req.getMobileNumber(), encPwd);
    }
}
