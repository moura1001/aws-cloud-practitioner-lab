package com.example.template.consumer.infrastructure.tracing;

/**
 * Guarda, por thread, o traceId que deve ser usado ao criar um span root.
 *
 * <p>Usado para que o span do consumer seja um root (sem parent) mas permaneça no
 * MESMO trace da API (via {@link ForcedTraceIdGenerator}). O
 * {@link io.opentelemetry.sdk.trace.IdGenerator} só é consultado na criação de
 * spans sem parent, então o valor é setado/limpo ao redor da criação do span.
 */
public final class ForcedTraceIdHolder {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    private ForcedTraceIdHolder() {
    }

    public static void set(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static String get() {
        return TRACE_ID.get();
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
