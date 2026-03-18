package com.nrl.analytics.model;

/**
 * Represents the side of the field where a try is scored or predicted.
 */
public enum Side {
    LEFT(0),
    RIGHT(1),
    MIDDLE(2);

    private final int code;

    Side(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Side fromCode(int code) {
        for (Side s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown side code: " + code);
    }

    public static Side fromString(String value) {
        return valueOf(value.toUpperCase());
    }
}
