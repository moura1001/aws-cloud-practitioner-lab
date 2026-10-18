package com.example.template.consumer.domain.port.out;

import com.example.template.consumer.infrastructure.adapter.out.http.ExternalServiceException;

public interface ExternalServicePort {
    String process(String payload) throws ExternalServiceException;
}