package com.example.template.api.domain.port.in;

import com.example.template.api.application.dto.EventRequest;
import com.example.template.api.application.dto.EventResponse;

public interface ProcessEventUseCase {
    EventResponse process(EventRequest request);
}