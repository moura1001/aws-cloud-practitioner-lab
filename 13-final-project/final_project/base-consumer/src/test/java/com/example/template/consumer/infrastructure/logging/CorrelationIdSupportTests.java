package com.example.template.consumer.infrastructure.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdSupportTests {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldEnsureCorrelationIdWhenMdcIsEmpty() {
        boolean added = CorrelationIdSupport.ensure();

        assertThat(added).isTrue();
        assertThat(MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY)).isNotNull();
        assertThat(MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY)).isNotBlank();
    }

    @Test
    void shouldNotOverwriteExistingCorrelationId() {
        MDC.put(CorrelationIdSupport.CORRELATION_ID_KEY, "existing-id");

        boolean added = CorrelationIdSupport.ensure();

        assertThat(added).isFalse();
        assertThat(MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY)).isEqualTo("existing-id");
    }

    @Test
    void shouldEnsureFromProvidedCorrelationId() {
        boolean added = CorrelationIdSupport.ensureFrom("provided-id");

        assertThat(added).isTrue();
        assertThat(MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY)).isEqualTo("provided-id");
    }

    @Test
    void shouldGenerateUuidWhenEnsureFromNull() {
        boolean added = CorrelationIdSupport.ensureFrom(null);

        assertThat(added).isTrue();
        String correlationId = MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY);
        assertThat(correlationId).isNotNull();
        assertThat(correlationId).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void shouldForcePutOverwriteExistingValue() {
        MDC.put(CorrelationIdSupport.CORRELATION_ID_KEY, "old-id");

        CorrelationIdSupport.forcePut("new-id");

        assertThat(MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY)).isEqualTo("new-id");
    }

    @Test
    void shouldForcePutGenerateUuidWhenNull() {
        CorrelationIdSupport.forcePut(null);

        String correlationId = MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY);
        assertThat(correlationId).isNotNull();
        assertThat(correlationId).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void shouldClearIfAddedWhenTrue() {
        MDC.put(CorrelationIdSupport.CORRELATION_ID_KEY, "some-id");

        CorrelationIdSupport.clearIfAdded(true);

        assertThat(MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY)).isNull();
    }

    @Test
    void shouldNotClearIfAddedWhenFalse() {
        MDC.put(CorrelationIdSupport.CORRELATION_ID_KEY, "some-id");

        CorrelationIdSupport.clearIfAdded(false);

        assertThat(MDC.get(CorrelationIdSupport.CORRELATION_ID_KEY)).isEqualTo("some-id");
    }
}