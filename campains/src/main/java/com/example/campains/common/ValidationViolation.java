package com.example.campains.common;

public record ValidationViolation(
        String field,
        String message
) {
}
