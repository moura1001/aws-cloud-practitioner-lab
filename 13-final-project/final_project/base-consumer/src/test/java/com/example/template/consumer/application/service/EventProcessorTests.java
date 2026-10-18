package com.example.template.consumer.application.service;

import com.example.template.consumer.domain.port.out.CachePort;
import com.example.template.consumer.infrastructure.adapter.out.http.ExternalServiceRetryExecutor;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventProcessorTests {

    @Mock
    private ExternalServiceRetryExecutor externalServiceRetryExecutor;

    @Mock
    private CachePort cachePort;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventProcessor eventProcessor;

    @Test
    void shouldProcessEventSuccessfully() throws Exception {
        String messageBody =
                "{\"eventId\":\"evt-1\",\"type\":\"DATA\",\"data\":\"hello\",\"timestamp\":\"2024-01-01T00:00:00Z\"}";

        var messageDto = new com.example.template.consumer.application.dto.EventMessageDto(
                "evt-1", "DATA", "hello", "2024-01-01T00:00:00Z"
        );

        when(objectMapper.readValue(
                messageBody,
                com.example.template.consumer.application.dto.EventMessageDto.class
        )).thenReturn(messageDto);

        eventProcessor.handleEvent(messageBody);

        verify(cachePort).put(eq("event:evt-1"), eq("PROCESSING"), eq(3600L));
        verify(externalServiceRetryExecutor).execute("HELLO");
        verify(cachePort).put(eq("event:evt-1"), eq("COMPLETED"), eq(3600L));
    }

    @Test
    void shouldThrowWhenExternalServiceFails() throws Exception {
        String messageBody =
                "{\"eventId\":\"evt-2\",\"type\":\"DATA\",\"data\":\"test\",\"timestamp\":\"2024-01-01T00:00:00Z\"}";

        var messageDto = new com.example.template.consumer.application.dto.EventMessageDto(
                "evt-2", "DATA", "test", "2024-01-01T00:00:00Z"
        );

        when(objectMapper.readValue(
                messageBody,
                com.example.template.consumer.application.dto.EventMessageDto.class
        )).thenReturn(messageDto);

        org.mockito.Mockito.doThrow(
                new RuntimeException("External service down")
        ).when(externalServiceRetryExecutor).execute("TEST");

        assertThatThrownBy(() -> eventProcessor.handleEvent(messageBody))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao processar evento");

        verify(cachePort).put(eq("event:evt-2"), eq("PROCESSING"), eq(3600L));
    }
}