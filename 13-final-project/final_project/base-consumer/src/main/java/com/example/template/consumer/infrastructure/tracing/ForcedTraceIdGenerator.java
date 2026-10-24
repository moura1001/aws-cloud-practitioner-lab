package com.example.template.consumer.infrastructure.tracing;

import io.opentelemetry.sdk.trace.IdGenerator;

/**
 * {@link IdGenerator} que permite forçar o traceId de um span root.
 *
 * <p>Quando há um traceId no {@link ForcedTraceIdHolder} (span root do consumer),
 * ele é usado; caso contrário delega para o gerador aleatório padrão. Assim, spans
 * root "normais" (ex.: schedulers) continuam com traceId aleatório.
 */
public final class ForcedTraceIdGenerator implements IdGenerator {

    private final IdGenerator delegate = IdGenerator.random();

    @Override
    public String generateTraceId() {
        String forced = ForcedTraceIdHolder.get();
        return forced != null ? forced : delegate.generateTraceId();
    }

    @Override
    public String generateSpanId() {
        return delegate.generateSpanId();
    }
}
