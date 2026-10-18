package com.example.template.consumer.infrastructure.resilience.circuit;

import com.example.template.consumer.infrastructure.config.circuit.BreakerProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CircuitBreakerService {

    private static final String HASH_KEY_STATE = "state";
    private static final String HASH_KEY_FAILURE_COUNT = "failureCount";

    private final StringRedisTemplate redisTemplate;
    private final BreakerProperties properties;

    public CircuitBreakerService(StringRedisTemplate redisTemplate, BreakerProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        if (properties.getFailureThreshold() <= 0) {
            throw new IllegalArgumentException("failureThreshold must be > 0");
        }
    }

    private HashOperations<String, Object, Object> hashOps() {
        return redisTemplate.opsForHash();
    }

    public boolean isOpen() {
        Object state = hashOps().get(properties.getKey(), HASH_KEY_STATE);
        return CircuitState.OPEN == CircuitState.fromValue(String.valueOf(state));
    }

    public boolean isHalfOpen() {
        Object state = hashOps().get(properties.getKey(), HASH_KEY_STATE);
        return CircuitState.HALF_OPEN == CircuitState.fromValue(String.valueOf(state));
    }

    public boolean isClosed() {
        Object state = hashOps().get(properties.getKey(), HASH_KEY_STATE);
        return state == null || CircuitState.CLOSED == CircuitState.fromValue(String.valueOf(state));
    }

    public long getNextProbeAt() {
        Object value = hashOps().get(properties.getKey(), "nextProbeAt");
        if (value == null) return Long.MIN_VALUE;
        return Long.parseLong(String.valueOf(value));
    }

    public void markSuccess() {
        long now = Instant.now().toEpochMilli();

        Map<String, String> data = new HashMap<>();
        data.put(HASH_KEY_STATE, CircuitState.CLOSED.getValue());
        data.put(HASH_KEY_FAILURE_COUNT, "0");
        data.put("lastSuccessAt", String.valueOf(now));
        data.put("lastError", "");

        hashOps().putAll(properties.getKey(), data);
        redisTemplate.expire(properties.getKey(), 7, TimeUnit.DAYS);
    }

    public void markFailure(String errorMessage) {
        long now = Instant.now().toEpochMilli();
        String key = properties.getKey();

        long failureCount = Optional.ofNullable(hashOps().increment(key, HASH_KEY_FAILURE_COUNT, 1L))
                .orElse(0L);

        Map<String, String> auxiliaryData = new HashMap<>();
        auxiliaryData.put("lastFailureAt", String.valueOf(now));
        auxiliaryData.put("lastError", errorMessage == null ? "" : errorMessage);
        hashOps().putAll(key, auxiliaryData);

        if (failureCount >= properties.getFailureThreshold()) {
            openCircuit();
        }

        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    public int getFailureCount() {
        Object value = hashOps().get(properties.getKey(), HASH_KEY_FAILURE_COUNT);
        if (value == null) return 0;
        return Integer.parseInt(String.valueOf(value));
    }

    public void openCircuit() {
        long now = Instant.now().toEpochMilli();
        long nextProbeAt = now + properties.getOpenCooldownMs();

        Map<String, String> data = new HashMap<>();
        data.put(HASH_KEY_STATE, CircuitState.OPEN.getValue());
        data.put("openedAt", String.valueOf(now));
        data.put("nextProbeAt", String.valueOf(nextProbeAt));
        data.put("lastFailureAt", String.valueOf(now));

        hashOps().putAll(properties.getKey(), data);
        redisTemplate.expire(properties.getKey(), 7, TimeUnit.DAYS);
    }

    public void halfOpen() {
        long now = Instant.now().toEpochMilli();
        hashOps().put(properties.getKey(), HASH_KEY_STATE, CircuitState.HALF_OPEN.getValue());
        hashOps().put(properties.getKey(), "halfOpenAt", String.valueOf(now));
    }

    public void closeCircuit() {
        markSuccess();
    }

    public boolean canProbeNow() {
        if (!isOpen()) return false;
        long nextProbeAt = getNextProbeAt();
        if (nextProbeAt <= 0) return false;
        long now = Instant.now().toEpochMilli();
        return now >= nextProbeAt;
    }

    public boolean shouldProbe() {
        return isOpen() && canProbeNow();
    }

    public void reset() {
        redisTemplate.delete(properties.getKey());
    }
}