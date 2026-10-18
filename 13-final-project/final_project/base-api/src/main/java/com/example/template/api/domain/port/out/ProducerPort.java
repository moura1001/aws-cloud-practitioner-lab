package com.example.template.api.domain.port.out;

public interface ProducerPort {
    void publish(String queueUrl, String messageBody);
}