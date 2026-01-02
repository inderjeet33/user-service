package com.prerana.userservice.exceptions;

public class UserAlreadyExistException extends BaseException {

    public UserAlreadyExistException() {
        super("INVALID_NUMBER", "User already exists with this mobile number");
    }

    public UserAlreadyExistException(String message) {
        super("INVALID_CREDENTIALS", message);
    }
}
