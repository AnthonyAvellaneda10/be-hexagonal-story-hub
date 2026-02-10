package com.uni.pe.storyhub.infrastructure.exception;

import lombok.Getter;

@Getter
public class LoginLockoutException extends RuntimeException {
    private final int idToast;
    private final int statusCode;

    public LoginLockoutException(String message, int idToast, int statusCode) {
        super(message);
        this.idToast = idToast;
        this.statusCode = statusCode;
    }
}
