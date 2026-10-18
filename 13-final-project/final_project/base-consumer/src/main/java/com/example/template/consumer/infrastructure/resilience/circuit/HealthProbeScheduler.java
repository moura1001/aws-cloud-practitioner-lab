package com.example.template.consumer.infrastructure.resilience.circuit;

import com.example.template.consumer.infrastructure.adapter.out.http.ExternalServiceAdapter;
import com.example.template.consumer.infrastructure.config.circuit.BreakerProperties;
import com.example.template.consumer.infrastructure.resilience.sqs.SqsListenerControlService;
import com.example.template.consumer.infrastructure.resilience.sqs.SqsListenerSyncScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class HealthProbeScheduler {

    private static final Logger log = LoggerFactory.getLogger(HealthProbeScheduler.class);

    private final CircuitBreakerService circuitBreakerService;
    private final SqsListenerControlService listenerControlService;
    private final SqsListenerSyncScheduler syncScheduler;
    private final ExternalServiceAdapter externalServiceAdapter;
    private final StringRedisTemplate redisTemplate;
    private final BreakerProperties properties;

    private final String instanceId = UUID.randomUUID().toString();

    public HealthProbeScheduler(
            CircuitBreakerService circuitBreakerService,
            SqsListenerControlService listenerControlService,
            SqsListenerSyncScheduler syncScheduler,
            ExternalServiceAdapter externalServiceAdapter,
            StringRedisTemplate redisTemplate,
            BreakerProperties properties
    ) {
        this.circuitBreakerService = circuitBreakerService;
        this.listenerControlService = listenerControlService;
        this.syncScheduler = syncScheduler;
        this.externalServiceAdapter = externalServiceAdapter;
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${app.circuit-breaker.probe-interval-ms:10000}")
    public void probe() {
        if (!circuitBreakerService.shouldProbe()) {
            return;
        }

        final String lockKey = probeLockKey();
        final Duration lockTtl = Duration.ofMillis(properties.getProbeLockTtlMs());

        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, instanceId, lockTtl);
        if (!Boolean.TRUE.equals(acquired)) {
            log.debug("[PROBE] Lock '{}' already held by another instance; skipping cycle.", lockKey);
            return;
        }

        log.debug("[PROBE] Lock '{}' acquired by instance {}", lockKey, instanceId);

        try {
            circuitBreakerService.halfOpen();
            externalServiceAdapter.probeHealth();
            circuitBreakerService.closeCircuit();
            listenerControlService.resumeAllInputListeners("Circuit CLOSED");
            syncScheduler.clearPausedByCircuit();
        } catch (Exception ex) {
            circuitBreakerService.openCircuit();
            listenerControlService.pauseAllInputListeners("Circuit OPEN");
            syncScheduler.markPausedByCircuit();
        } finally {
            releaseLock(lockKey);
        }
    }

    private void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception ex) {
            log.warn("[PROBE] Failed to release lock '{}'. TTL will reclaim it. error={}",
                    lockKey, ex.getMessage());
        }
    }

    private String probeLockKey() {
        return properties.getKey() + ":probe-lock";
    }
}