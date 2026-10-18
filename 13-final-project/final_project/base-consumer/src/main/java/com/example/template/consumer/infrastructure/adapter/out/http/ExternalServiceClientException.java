package com.example.template.consumer.infrastructure.adapter.out.http;

public class ExternalServiceClientException extends RuntimeException {
    public ExternalServiceClientException(String message) {
        super(message);
    }

    public ExternalServiceClientException(String message, Throwable cause) {
        super(message, cause);
    }
}