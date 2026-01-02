package com.prerana.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(BaseException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", ex.getErrorCode());
        response.put("message", ex.getMessage());
        response.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
