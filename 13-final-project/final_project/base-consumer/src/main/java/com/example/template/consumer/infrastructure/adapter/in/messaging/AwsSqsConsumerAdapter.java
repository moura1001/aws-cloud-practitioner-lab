package com.example.template.consumer.infrastructure.adapter.in.messaging;

import com.example.template.consumer.application.dto.EventMessageDto;
import com.example.template.consumer.domain.port.in.ConsumerPort;
import com.example.template.consumer.domain.port.out.CachePort;
import com.example.template.consumer.infrastructure.logging.CorrelationIdSupport;
import com.example.template.consumer.infrastructure.tracing.ForcedTraceIdHolder;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.opentelemetry.api.trace.TraceId;
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
    private static final String TRACE_KEY_PREFIX = "event:trace:";
    private static final String SPAN_INPUT = "base-consumer-input";
    private static final String SPAN_DLQ = "base-consumer-dlq";

    private final ConsumerPort consumerPort;
    private final CachePort cachePort;
    private final ObjectMapper objectMapper;
    private final ObservationRegistry observationRegistry;

    public AwsSqsConsumerAdapter(ConsumerPort consumerPort, CachePort cachePort,
                                 ObjectMapper objectMapper, ObservationRegistry observationRegistry) {
        this.consumerPort = consumerPort;
        this.cachePort = cachePort;
        this.objectMapper = objectMapper;
        this.observationRegistry = observationRegistry;
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

        Observation observation = startConsumerObservation(SPAN_INPUT, resolveTraceId(eventId), eventId);
        try (Observation.Scope scope = observation.openScope()) {
            consumerPort.handleEvent(body);
        } finally {
            CorrelationIdSupport.clearIfAdded(correlationIdAdded);
            observation.stop();
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

        Observation observation = startConsumerObservation(SPAN_DLQ, resolveTraceId(eventId), eventId);
        try (Observation.Scope scope = observation.openScope()) {
            consumerPort.handleEvent(body);
        } finally {
            CorrelationIdSupport.clearIfAdded(correlationIdAdded);
            observation.stop();
        }
    }

    /**
     * Cria (start) o span do consumer como um <b>root no mesmo trace da API</b>.
     *
     * <p>O traceId é lido do Redis ({@code event:trace:<eventId>}), gravado pela API no início
     * do fluxo — não há propagação W3C pela fila. O span é uma {@link Observation}, o que mantém
     * as operações instrumentadas (Redis/Valkey, HTTP client) aninhadas sob ele.
     *
     * <p>Sem traceId no Redis, o root gera um traceId próprio (aleatório).
     */
    private Observation startConsumerObservation(String spanName, String traceId, String eventId) {
        ForcedTraceIdHolder.set(TraceId.isValid(traceId) ? traceId : null);
        try {
            return Observation.createNotStarted(spanName, observationRegistry)
                    .lowCardinalityKeyValue(CORRELATION_ID, eventId)
                    .start();
        } finally {
            ForcedTraceIdHolder.clear();
        }
    }

    private String resolveTraceId(String eventId) {
        try {
            return cachePort.get(TRACE_KEY_PREFIX + eventId);
        } catch (RuntimeException e) {
            log.warn("[TRACE] Falha ao obter traceId do Redis eventId={}: {}", eventId, e.getMessage());
            return null;
        }
    }

    private String resolveEventId(String correlationId) {
        if (correlationId == null || correlationId.isBlank() || "No Correlation ID".equalsIgnoreCase(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
