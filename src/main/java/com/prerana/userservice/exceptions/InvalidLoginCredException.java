package com.prerana.userservice.exceptions;

public class InvalidLoginCredException extends BaseException {

    public InvalidLoginCredException() {
        super("INVALID_LOGIN", "Invalid username or password");
    }

    public InvalidLoginCredException(String message) {
        super("INVALID_LOGIN", message);
    }
}
