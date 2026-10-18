package com.example.template.consumer.domain.port.in;

public interface ConsumerPort {
    void handleEvent(String messageBody);
}