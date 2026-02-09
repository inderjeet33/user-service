package com.prerana.userservice.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
    INDIVIDUAL("INDIVIDUAL"),
    CSR("CSR"),
    NGO("NGO"),
    MODERATOR("MODERATOR");

    private final String value;

    private static final Map<String, UserType> MAP = new HashMap<>();
    static {
        for (UserType type : UserType.values()) {
            MAP.put(type.value, type);
        }
    }

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserType fromString(String value) {
        if (value == null) return null;
        return MAP.get(value.toUpperCase());  // case-insensitive
    }
}
