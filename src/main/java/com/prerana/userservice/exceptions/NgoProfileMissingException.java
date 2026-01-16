package com.prerana.userservice.exceptions;

public class NgoProfileMissingException extends BaseException{

    public NgoProfileMissingException() {
        super("NGO_PROFILE_MISSING", "Profile is missing for this NGO");
    }

    public NgoProfileMissingException(String message) {
        super("NGO_PROFILE_MISSING", message);
    }
}
