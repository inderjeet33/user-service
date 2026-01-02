package com.prerana.userservice.exceptions;

public class MobileNumberOTPNotVerified extends BaseException {

    public MobileNumberOTPNotVerified() {
        super("INVALID_LOGIN", "Invalid username or password");
    }

    public MobileNumberOTPNotVerified(String message) {
        super("INVALID_LOGIN", message);
    }
}
