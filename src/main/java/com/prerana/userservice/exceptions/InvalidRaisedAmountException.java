package com.prerana.userservice.exceptions;

public class InvalidRaisedAmountException extends  BaseException{

    public InvalidRaisedAmountException() {
        super("INVALID_RAISED_AMOUNT", "Invalid raised amount");
    }

    public InvalidRaisedAmountException(String message) {
        super("INVALID_RAISED_AMOUNT", message);
    }

}

