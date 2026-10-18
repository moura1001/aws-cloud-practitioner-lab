package com.example.template.consumer.infrastructure.adapter.in.messaging;

import com.example.template.consumer.application.dto.EventMessageDto;
import com.example.template.consumer.domain.port.in.ConsumerPort;
import com.example.template.consumer.infrastructure.logging.CorrelationIdSupport;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class AwsSqsConsumerAdapter {
    private static final Logger log = LoggerFactory.getLogger(AwsSqsConsumerAdapter.class);
    private static final String INPUT_QUEUE = "input-queue";
    private static final String DLQ_QUEUE = "dlq-queue";
    private static final String CORRELATION_ID = "correlationId";

    private final ConsumerPort consumerPort;
    private final Tracer otelTracer;
    private final ObjectMapper objectMapper;

    public AwsSqsConsumerAdapter(ConsumerPort consumerPort, OpenTelemetry openTelemetry, ObjectMapper objectMapper) {
        this.consumerPort = consumerPort;
        this.otelTracer = openTelemetry.getTracerProvider().get("template-consumer");
        this.objectMapper = objectMapper;
    }

    @SqsListener(
            value = "${aws.sqs.queues.input-queue}",
            id = "inputQueueListener",
            messageVisibilitySeconds = "${aws.sqs.visibility-timeout-seconds:90}",
            maxMessagesPerPoll = "${aws.sqs.max-messages-per-poll:10}"
    )
    public void onInputMessage(@Payload String body) {
        log.info("[SQS][{}] Received: {}", INPUT_QUEUE, body);

        EventMessageDto eventMessage = objectMapper.readValue(body, EventMessageDto.class);
        String eventId = resolveEventId(eventMessage.getEventId());
        boolean correlationIdAdded = CorrelationIdSupport.ensure();

        Span span = startTraceSpan("sqs.receive " + INPUT_QUEUE, eventId);
        try (var scope = span.makeCurrent()) {
            consumerPort.handleEvent(body);
        } finally {
            CorrelationIdSupport.clearIfAdded(correlationIdAdded);
            span.end();
        }
    }

    @SqsListener(
            value = "${aws.sqs.queues.dlq-queue}",
            id = "dlqQueueListener",
            messageVisibilitySeconds = "${aws.sqs.visibility-timeout-seconds:90}",
            maxMessagesPerPoll = "${aws.sqs.max-messages-per-poll:10}"
    )
    public void onDlqMessage(@Payload String body) {
        log.info("[SQS][{}] Received (DLQ): {}", DLQ_QUEUE, body);

        EventMessageDto eventMessage = objectMapper.readValue(body, EventMessageDto.class);
        String eventId = resolveEventId(eventMessage.getEventId());
        boolean correlationIdAdded = CorrelationIdSupport.ensure();

        Span span = startTraceSpan("sqs.receive " + DLQ_QUEUE, eventId);
        try (var scope = span.makeCurrent()) {
            consumerPort.handleEvent(body);
        } finally {
            CorrelationIdSupport.clearIfAdded(correlationIdAdded);
            span.end();
        }
    }

    private Span startTraceSpan(String spanName, String correlationId) {
        Span span = otelTracer.spanBuilder(spanName)
                .startSpan();
        span.setAttribute(CORRELATION_ID, correlationId);
        return span;
    }

    private String resolveEventId(String correlationId) {
        if (correlationId == null || correlationId.isBlank() || "No Correlation ID".equalsIgnoreCase(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }
}