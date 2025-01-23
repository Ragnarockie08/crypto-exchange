package com.crypto.currency.exception.error;

public class ApiException extends RuntimeException {

    private String value;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Exception e) {
        super(message);
    }


}

