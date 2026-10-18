package com.example.template.api.application.service;

import com.example.template.api.application.dto.EventRequest;
import com.example.template.api.application.dto.EventResponse;
import com.example.template.api.application.dto.SqsMessageDto;
import com.example.template.api.domain.exception.InvalidPayloadException;
import com.example.template.api.domain.port.in.ProcessEventUseCase;
import com.example.template.api.domain.port.out.OperationStatusPort;
import com.example.template.api.domain.port.out.ProducerPort;
import com.example.template.api.domain.valueobject.OperationStatus;
import com.example.template.api.infrastructure.config.aws.SqsProperties;
import com.example.template.api.infrastructure.logging.CorrelationIdSupport;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventOrchestrator implements ProcessEventUseCase {

    private final ProducerPort producerPort;
    private final OperationStatusPort operationStatusPort;
    private final SqsProperties sqsProperties;
    private final ObjectMapper objectMapper;

    @Override
    public EventResponse process(EventRequest request) {
        validate(request);

        String eventId = resolveEventId(request.getId());
        CorrelationIdSupport.forcePut(eventId);

        log.info("[API] Evento recebido eventId={}", eventId);

        SqsMessageDto message = SqsMessageDto.builder()
                .eventId(eventId)
                .type(request.getType())
                .data(request.getData())
                .timestamp(Instant.now().toString())
                .build();

        String queueUrl = resolveInputQueueUrl();
        String json = serialize(message);

        try {
            operationStatusPort.setStatus(eventId, OperationStatus.PENDING.name());
            producerPort.publish(queueUrl, json);
            operationStatusPort.setStatus(eventId, OperationStatus.PROCESSING.name());
        } catch (RuntimeException e) {
            operationStatusPort.setStatus(eventId, OperationStatus.FAILED.name());
            throw new IllegalStateException("Falha ao publicar mensagem na fila", e);
        }

        log.info("[API] Mensagem publicada na fila eventId={}", eventId);

        return EventResponse.builder()
                .id(eventId)
                .status(OperationStatus.PROCESSING.name())
                .message("Evento recebido e encaminhado para processamento")
                .build();
    }

    private void validate(EventRequest request) {
        if (request.getType() == null || request.getType().isBlank()) {
            throw new InvalidPayloadException("type é obrigatório");
        }
        if (request.getData() == null || request.getData().isBlank()) {
            throw new InvalidPayloadException("data é obrigatório");
        }
    }

    private String resolveEventId(String id) {
        return id != null && !id.isBlank() ? id.trim() : UUID.randomUUID().toString();
    }

    private String resolveInputQueueUrl() {
        String url = sqsProperties.getQueues().getInputQueue();
        if (url == null || url.isBlank()) {
            throw new InvalidPayloadException("URL da fila de entrada não configurada");
        }
        return url;
    }

    private String serialize(SqsMessageDto message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JacksonException e) {
            throw new InvalidPayloadException("Falha ao serializar mensagem SQS: " + e.getMessage());
        }
    }
}