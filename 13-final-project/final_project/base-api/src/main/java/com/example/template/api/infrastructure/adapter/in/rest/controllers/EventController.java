package com.example.template.api.infrastructure.adapter.in.rest.controllers;

import com.example.template.api.application.dto.EventRequest;
import com.example.template.api.application.dto.EventResponse;
import com.example.template.api.domain.port.in.ProcessEventUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final ProcessEventUseCase processEventUseCase;

    public EventController(ProcessEventUseCase processEventUseCase) {
        this.processEventUseCase = processEventUseCase;
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> postEvent(@RequestBody EventRequest request) {
        EventResponse response = processEventUseCase.process(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}