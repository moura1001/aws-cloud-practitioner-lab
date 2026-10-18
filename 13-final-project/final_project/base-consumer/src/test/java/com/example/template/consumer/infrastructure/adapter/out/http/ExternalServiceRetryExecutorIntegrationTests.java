package com.example.template.consumer.infrastructure.adapter.out.http;

import com.example.template.consumer.infrastructure.resilience.circuit.CircuitBreakerService;
import com.example.template.consumer.infrastructure.resilience.sqs.SqsListenerControlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(
        ExternalServiceRetryExecutorIntegrationTests.TestConfig.class
)
class ExternalServiceRetryExecutorIntegrationTests {

    @Configuration
    @EnableRetry
    static class TestConfig {

        @Bean
        ExternalServiceAdapter externalServiceAdapter() {
            return mock(ExternalServiceAdapter.class);
        }

        @Bean
        CircuitBreakerService circuitBreakerService() {
            return mock(CircuitBreakerService.class);
        }

        @Bean
        SqsListenerControlService sqsListenerControlService() {
            return mock(SqsListenerControlService.class);
        }

        @Bean
        ExternalServiceRetryExecutor externalServiceRetryExecutor(
                ExternalServiceAdapter adapter,
                CircuitBreakerService circuitBreakerService,
                SqsListenerControlService listenerControlService
        ) {
            return new ExternalServiceRetryExecutor(
                    adapter,
                    circuitBreakerService,
                    listenerControlService
            );
        }
    }

    @Autowired
    private ExternalServiceRetryExecutor executor;

    @Autowired
    private ExternalServiceAdapter externalServiceAdapter;

    @Autowired
    private CircuitBreakerService circuitBreakerService;

    @Autowired
    private SqsListenerControlService sqsListenerControlService;

    private final AtomicInteger failedCalls = new AtomicInteger();

    @BeforeEach
    void setUp() {
        reset(
                externalServiceAdapter,
                circuitBreakerService,
                sqsListenerControlService
        );

        failedCalls.set(0);

        when(circuitBreakerService.isOpen())
                .thenAnswer(invocation -> failedCalls.get() >= 5);

        doAnswer(invocation -> {
            failedCalls.incrementAndGet();
            return null;
        }).when(circuitBreakerService).markFailure(anyString());

        when(externalServiceAdapter.process(anyString()))
                .thenThrow(new ExternalServiceException(
                        "Simulated external service failure"
                ));
    }

    @Test
    void shouldOpenCircuitAfterFiveFailedExternalCalls() {
        // First operation: four failed external calls.
        assertThatThrownBy(() -> executor.execute("event-1"))
                .isInstanceOf(ExternalServiceRetryableException.class);

        assertThat(failedCalls.get()).isEqualTo(4);

        // Second operation: fifth failure opens the circuit.
        assertThatThrownBy(() -> executor.execute("event-2"))
                .isInstanceOf(ExternalServiceCircuitOpenException.class);

        assertThat(failedCalls.get()).isEqualTo(5);

        verify(externalServiceAdapter, times(5)).process(anyString());
        verify(circuitBreakerService, times(5))
                .markFailure(anyString());

        verify(sqsListenerControlService, times(1))
                .pauseAllInputListeners("Circuit OPEN");
    }
}
