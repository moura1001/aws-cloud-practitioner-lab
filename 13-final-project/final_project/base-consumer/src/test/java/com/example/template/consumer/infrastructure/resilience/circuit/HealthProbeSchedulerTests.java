package com.example.template.consumer.infrastructure.resilience.circuit;

import com.example.template.consumer.infrastructure.adapter.out.http.ExternalServiceAdapter;
import com.example.template.consumer.infrastructure.config.circuit.BreakerProperties;
import com.example.template.consumer.infrastructure.resilience.sqs.SqsListenerControlService;
import com.example.template.consumer.infrastructure.resilience.sqs.SqsListenerSyncScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.*;

class HealthProbeSchedulerTests {

    private CircuitBreakerService circuitBreakerService;
    private SqsListenerControlService listenerControlService;
    private SqsListenerSyncScheduler syncScheduler;
    private ExternalServiceAdapter externalServiceAdapter;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private BreakerProperties properties;

    private HealthProbeScheduler scheduler;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        circuitBreakerService = mock(CircuitBreakerService.class);
        listenerControlService = mock(SqsListenerControlService.class);
        syncScheduler = mock(SqsListenerSyncScheduler.class);
        externalServiceAdapter = mock(ExternalServiceAdapter.class);
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        properties = new BreakerProperties();

        properties.setKey("test:external:circuit");
        properties.setProbeLockTtlMs(8000);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        scheduler = new HealthProbeScheduler(
                circuitBreakerService,
                listenerControlService,
                syncScheduler,
                externalServiceAdapter,
                redisTemplate,
                properties
        );
    }

    @Test
    void shouldSkipProbeWhenCircuitIsNotReady() {
        when(circuitBreakerService.shouldProbe()).thenReturn(false);

        scheduler.probe();

        verify(circuitBreakerService).shouldProbe();
        verifyNoInteractions(externalServiceAdapter, listenerControlService, syncScheduler);
        verify(redisTemplate, never()).opsForValue();
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void shouldCloseCircuitAndResumeListenersWhenHealthCheckSucceeds() {
        when(circuitBreakerService.shouldProbe()).thenReturn(true);
        when(valueOperations.setIfAbsent(
                eq("test:external:circuit:probe-lock"),
                anyString(),
                eq(Duration.ofMillis(8000))
        )).thenReturn(true);

        scheduler.probe();

        verify(circuitBreakerService).halfOpen();
        verify(externalServiceAdapter).probeHealth();
        verify(circuitBreakerService).closeCircuit();
        verify(listenerControlService).resumeAllInputListeners("Circuit CLOSED");
        verify(syncScheduler).clearPausedByCircuit();

        verify(circuitBreakerService, never()).openCircuit();
        verify(listenerControlService, never()).pauseAllInputListeners(anyString());
        verify(syncScheduler, never()).markPausedByCircuit();

        verify(redisTemplate).delete("test:external:circuit:probe-lock");
    }

    @Test
    void shouldReopenCircuitAndPauseListenersWhenHealthCheckFails() {
        when(circuitBreakerService.shouldProbe()).thenReturn(true);
        when(valueOperations.setIfAbsent(
                eq("test:external:circuit:probe-lock"),
                anyString(),
                eq(Duration.ofMillis(8000))
        )).thenReturn(true);

        doThrow(new IllegalStateException("Health check failed"))
                .when(externalServiceAdapter)
                .probeHealth();

        scheduler.probe();

        verify(circuitBreakerService).halfOpen();
        verify(externalServiceAdapter).probeHealth();
        verify(circuitBreakerService).openCircuit();
        verify(listenerControlService).pauseAllInputListeners("Circuit OPEN");
        verify(syncScheduler).markPausedByCircuit();

        verify(circuitBreakerService, never()).closeCircuit();
        verify(listenerControlService, never()).resumeAllInputListeners(anyString());
        verify(syncScheduler, never()).clearPausedByCircuit();

        verify(redisTemplate).delete("test:external:circuit:probe-lock");
    }

    @Test
    void shouldSkipProbeWhenLockIsAlreadyHeld() {
        when(circuitBreakerService.shouldProbe()).thenReturn(true);
        when(valueOperations.setIfAbsent(
                eq("test:external:circuit:probe-lock"),
                anyString(),
                eq(Duration.ofMillis(8000))
        )).thenReturn(false);

        scheduler.probe();

        verifyNoInteractions(externalServiceAdapter, listenerControlService, syncScheduler);
        verify(circuitBreakerService, never()).halfOpen();
        verify(circuitBreakerService, never()).closeCircuit();
        verify(circuitBreakerService, never()).openCircuit();

        // Não pode apagar o lock que pertence a outra instância.
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void shouldReleaseLockWhenHealthCheckThrowsAnException() {
        when(circuitBreakerService.shouldProbe()).thenReturn(true);
        when(valueOperations.setIfAbsent(
                eq("test:external:circuit:probe-lock"),
                anyString(),
                eq(Duration.ofMillis(8000))
        )).thenReturn(true);

        doThrow(new IllegalStateException("Health check failed"))
                .when(externalServiceAdapter)
                .probeHealth();

        scheduler.probe();

        // O método captura a falha, mas deve liberar o lock no finally.
        verify(redisTemplate).delete("test:external:circuit:probe-lock");
    }
}
