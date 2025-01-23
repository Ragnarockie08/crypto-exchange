package com.crypto.currency.exception.error;

import lombok.Getter;

@Getter
public class ProviderException extends RuntimeException {

    private final String message;
    private final String detailedMessage;

    public ProviderException(String message) {
        super(message);
        this.message = message;
        this.detailedMessage = null;
    }

    public ProviderException(String message, Exception ex) {
        super(message);
        this.message = message;
        this.detailedMessage = ex.getLocalizedMessage();
    }
}
