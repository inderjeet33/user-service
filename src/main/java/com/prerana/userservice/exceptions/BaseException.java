package com.prerana.userservice.exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final String errorCode;

    public BaseException(String errorCode, String message) {
        super(message); // RuntimeException message
        this.errorCode = errorCode;
    }
}
