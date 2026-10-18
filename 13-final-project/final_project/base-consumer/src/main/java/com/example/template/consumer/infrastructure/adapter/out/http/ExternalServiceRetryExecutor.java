package com.example.template.consumer.infrastructure.adapter.out.http;

import com.example.template.consumer.infrastructure.resilience.circuit.CircuitBreakerService;
import com.example.template.consumer.infrastructure.resilience.sqs.SqsListenerControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternalServiceRetryExecutor {

    private final ExternalServiceAdapter externalServiceAdapter;
    private final CircuitBreakerService circuitBreakerService;
    private final SqsListenerControlService sqsListenerControlService;

    public ExternalServiceRetryExecutor(ExternalServiceAdapter externalServiceAdapter,
                                         CircuitBreakerService circuitBreakerService,
                                         SqsListenerControlService sqsListenerControlService) {
        this.externalServiceAdapter = externalServiceAdapter;
        this.circuitBreakerService = circuitBreakerService;
        this.sqsListenerControlService = sqsListenerControlService;
    }

    @Retryable(
            retryFor = ExternalServiceRetryableException.class,
            maxAttempts = 4,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0,
                    maxDelay = 8000
            )
    )
    public String execute(String payload) {
        if (circuitBreakerService.isOpen()) {
            log.warn("[CIRCUIT] Circuit breaker OPEN. Fast-fail without calling external service.");
            throw new ExternalServiceCircuitOpenException("Circuit breaker is open. External service unavailable.");
        }

        try {
            String result = externalServiceAdapter.process(payload);
            circuitBreakerService.markSuccess();
            return result;
        } catch (ExternalServiceException ex) {
            log.error("[CIRCUIT] External service call failed: {}", ex.getMessage());
            circuitBreakerService.markFailure(ex.getMessage());
            if (circuitBreakerService.isOpen()) {
                sqsListenerControlService.pauseAllInputListeners("Circuit OPEN");
                log.error("[CIRCUIT] Circuit opened. Input listeners paused.");
            }
            throw new ExternalServiceRetryableException("External service failed with 5xx", ex);
        }
    }
}