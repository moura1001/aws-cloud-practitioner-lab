package com.example.template.consumer.infrastructure.adapter.out.http;

public class ExternalServiceRetryableException extends RuntimeException {
    public ExternalServiceRetryableException(String message) {
        super(message);
    }

    public ExternalServiceRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}