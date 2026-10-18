package com.example.template.consumer.infrastructure.logging;

import org.slf4j.MDC;

import java.util.UUID;

public final class CorrelationIdSupport {
    public static final String CORRELATION_ID_KEY = "correlationId";

    private CorrelationIdSupport() {
    }

    public static boolean ensure() {
        return ensureFrom(null);
    }

    public static boolean ensureFrom(String correlationId) {
        var existing = MDC.get(CORRELATION_ID_KEY);
        if (existing != null && !existing.isBlank()) {
            return false;
        }

        var value = (correlationId == null || correlationId.isBlank())
                ? UUID.randomUUID().toString()
                : correlationId.trim();
        MDC.put(CORRELATION_ID_KEY, value);
        return true;
    }

    public static void forcePut(String correlationId) {
        var value = (correlationId == null || correlationId.isBlank())
                ? UUID.randomUUID().toString()
                : correlationId.trim();
        MDC.put(CORRELATION_ID_KEY, value);
    }

    public static void clearIfAdded(boolean added) {
        if (added) {
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}