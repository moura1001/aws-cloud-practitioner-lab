package com.example.template.api.application.service;

import com.example.template.api.application.dto.EventRequest;
import com.example.template.api.application.dto.EventResponse;
import com.example.template.api.domain.exception.InvalidPayloadException;
import com.example.template.api.domain.port.out.OperationStatusPort;
import com.example.template.api.domain.port.out.ProducerPort;
import com.example.template.api.infrastructure.config.aws.SqsProperties;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventOrchestratorTests {

    @Mock
    private ProducerPort producerPort;

    @Mock
    private OperationStatusPort operationStatusPort;

    @Mock
    private SqsProperties sqsProperties;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventOrchestrator eventOrchestrator;

    @Test
    void shouldProcessValidEventAndReturnResponse() throws Exception {
        SqsProperties.Queues queues = new SqsProperties.Queues();
        queues.setInputQueue("http://localhost/input-queue");
        when(sqsProperties.getQueues()).thenReturn(queues);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventId\":\"123\",\"data\":\"test\"}");

        EventRequest request = EventRequest.builder()
                .id("123")
                .type("DATA")
                .data("some-data")
                .build();

        EventResponse response = eventOrchestrator.process(request);

        assertThat(response.getId()).isEqualTo("123");
        assertThat(response.getStatus()).isEqualTo("PROCESSING");
        assertThat(response.getMessage()).contains("Evento recebido");

        verify(operationStatusPort).setStatus(eq("123"), eq("PENDING"));
        verify(producerPort).publish(eq("http://localhost/input-queue"), anyString());
        verify(operationStatusPort).setStatus(eq("123"), eq("PROCESSING"));
    }

    @Test
    void shouldGenerateUuidWhenIdIsBlank() throws Exception {
        SqsProperties.Queues queues = new SqsProperties.Queues();
        queues.setInputQueue("http://localhost/input-queue");
        when(sqsProperties.getQueues()).thenReturn(queues);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventId\":\"generated\"}");

        EventRequest request = EventRequest.builder()
                .id("")
                .type("DATA")
                .data("some-data")
                .build();

        EventResponse response = eventOrchestrator.process(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getId()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void shouldThrowExceptionWhenDataIsBlank() {
        EventRequest request = EventRequest.builder()
                .id("123")
                .type("DATA")
                .data("")
                .build();

        assertThatThrownBy(() -> eventOrchestrator.process(request))
                .isInstanceOf(InvalidPayloadException.class)
                .hasMessageContaining("data é obrigatório");
    }

    @Test
    void shouldSetFailedStatusWhenProducerThrowsException() throws Exception {
        SqsProperties.Queues queues = new SqsProperties.Queues();
        queues.setInputQueue("http://localhost/input-queue");
        when(sqsProperties.getQueues()).thenReturn(queues);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventId\":\"123\"}");
        doThrow(new RuntimeException("SQS error")).when(producerPort).publish(anyString(), anyString());

        EventRequest request = EventRequest.builder()
                .id("123")
                .type("DATA")
                .data("some-data")
                .build();

        assertThatThrownBy(() -> eventOrchestrator.process(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Falha ao publicar mensagem na fila");

        verify(operationStatusPort).setStatus(eq("123"), eq("PENDING"));
        verify(operationStatusPort).setStatus(eq("123"), eq("FAILED"));
    }

    @Test
    void shouldGenerateUuidWhenIdIsNull() throws Exception {
        SqsProperties.Queues queues = new SqsProperties.Queues();
        queues.setInputQueue("http://localhost/input-queue");
        when(sqsProperties.getQueues()).thenReturn(queues);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventId\":\"generated\"}");

        EventRequest request = EventRequest.builder()
                .id(null)
                .type("DATA")
                .data("some-data")
                .build();

        EventResponse response = eventOrchestrator.process(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getId()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }
}