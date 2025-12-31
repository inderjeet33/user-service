package com.prerana.userservice.util;

import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(CharSequence sequence){
        return encoder.encode(sequence);
    }

    public boolean matches(String rawPassword,String storedPassword){
        return encoder.matches(rawPassword,storedPassword);
    }


}
