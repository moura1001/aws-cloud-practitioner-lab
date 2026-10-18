package com.example.template.api.domain.port.out;

public interface OperationStatusPort {
    void setStatus(String eventId, String status);

    String getStatus(String eventId);
}