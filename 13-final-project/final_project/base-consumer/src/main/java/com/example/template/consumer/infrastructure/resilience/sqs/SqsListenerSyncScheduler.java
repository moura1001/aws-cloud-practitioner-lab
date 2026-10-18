package com.example.template.consumer.infrastructure.resilience.sqs;

import com.example.template.consumer.infrastructure.resilience.circuit.CircuitBreakerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SqsListenerSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(SqsListenerSyncScheduler.class);

    private final CircuitBreakerService circuitBreakerService;
    private final SqsListenerControlService listenerControlService;

    private volatile boolean listenerPausedByCircuit = false;

    public SqsListenerSyncScheduler(
            CircuitBreakerService circuitBreakerService,
            SqsListenerControlService listenerControlService
    ) {
        this.circuitBreakerService = circuitBreakerService;
        this.listenerControlService = listenerControlService;
    }

    @Scheduled(fixedDelayString = "${app.circuit-breaker.sync-interval-ms:5000}")
    public void syncListenerState() {
        boolean circuitOpen = circuitBreakerService.isOpen();

        if (circuitOpen && !listenerPausedByCircuit) {
            log.warn("[SQS][SYNC] Circuit OPEN detected in Redis. Pausing listeners...");
            listenerControlService.pauseAllInputListeners("Circuit OPEN (sync from Redis)");
            listenerPausedByCircuit = true;
        } else if (!circuitOpen && listenerPausedByCircuit) {
            log.info("[SQS][SYNC] Circuit CLOSED detected in Redis. Resuming listeners...");
            listenerControlService.resumeAllInputListeners("Circuit CLOSED (sync from Redis)");
            listenerPausedByCircuit = false;
        }
    }

    public void markPausedByCircuit() {
        listenerPausedByCircuit = true;
    }

    public void clearPausedByCircuit() {
        listenerPausedByCircuit = false;
    }

    public boolean isListenerPausedByCircuit() {
        return listenerPausedByCircuit;
    }
}