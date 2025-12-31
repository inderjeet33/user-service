package com.prerana.userservice.filter;

import com.prerana.userservice.enums.UserType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserTypeGuard {

    public boolean check(UserType requiredType) {
        String userType = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .filter(a -> a.getAuthority().startsWith("TYPE_"))
                .findFirst()
                .orElseThrow()
                .getAuthority()
                .replace("TYPE_", "");

        return userType.equals(requiredType.name());
    }
}

