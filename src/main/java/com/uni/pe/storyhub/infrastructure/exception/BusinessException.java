package com.uni.pe.storyhub.infrastructure.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int idToast;
    private final int statusCode;

    public BusinessException(String message, int idToast, int statusCode) {
        super(message);
        this.idToast = idToast;
        this.statusCode = statusCode;
    }
}
