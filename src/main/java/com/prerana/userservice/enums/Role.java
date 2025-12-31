package com.prerana.userservice.enums;

import java.util.HashMap;
import java.util.Map;

public enum Role {

    USER("USER"),
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    SUPER_ADMIN("SUPER_ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    private static final Map<String, Role> MAP = new HashMap<>();
    static {
        for (Role type : Role.values()) {
            MAP.put(type.value, type);
        }
    }

    public static Role fromString(String value) {
        if (value == null) return null;
        return MAP.get(value.toUpperCase());  // case-insensitive
    }
}
