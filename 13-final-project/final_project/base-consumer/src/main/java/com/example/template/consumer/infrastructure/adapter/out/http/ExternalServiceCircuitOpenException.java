package com.example.template.consumer.infrastructure.adapter.out.http;

public class ExternalServiceCircuitOpenException extends RuntimeException {
    public ExternalServiceCircuitOpenException(String message) {
        super(message);
    }

    public ExternalServiceCircuitOpenException(Exception ex) {
        super(ex);
    }
}