package com.myauth.common.utils;

import org.springframework.http.HttpStatus;

public record Error(HttpStatus code, String message) {
    @Override
    public String toString() {
        return String.format(
            "Error { code: %d, message: '%s' }",
            code.value(),
            message
        );
    }
}
