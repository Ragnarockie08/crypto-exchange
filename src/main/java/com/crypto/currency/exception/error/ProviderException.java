package com.crypto.currency.exception.error;

import lombok.Getter;

@Getter
public class ProviderException extends RuntimeException {

    private final String message;

    public ProviderException(String message) {
        super(message);
        this.message = message;
    }
}
