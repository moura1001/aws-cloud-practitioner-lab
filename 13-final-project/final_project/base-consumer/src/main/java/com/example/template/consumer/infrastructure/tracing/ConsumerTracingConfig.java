package com.example.template.consumer.infrastructure.tracing;

import org.springframework.boot.micrometer.tracing.opentelemetry.autoconfigure.SdkTracerProviderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra o {@link ForcedTraceIdGenerator} no {@code SdkTracerProvider}.
 *
 * <p>Permite que o span do consumer seja criado como root (sem parent) reutilizando o
 * traceId propagado pela API (via Redis), permanecendo no mesmo trace. O gerador só força
 * o traceId quando {@link ForcedTraceIdHolder} está preenchido; caso contrário usa o aleatório.
 */
@Configuration
public class ConsumerTracingConfig {

    @Bean
    SdkTracerProviderBuilderCustomizer forcedTraceIdGeneratorCustomizer() {
        return builder -> builder.setIdGenerator(new ForcedTraceIdGenerator());
    }
}
