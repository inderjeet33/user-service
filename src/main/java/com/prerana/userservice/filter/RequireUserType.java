package com.prerana.userservice.filter;

import com.prerana.userservice.enums.UserType;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@userTypeGuard.check(#requiredType)")
public @interface RequireUserType {
    UserType requiredType();
}
