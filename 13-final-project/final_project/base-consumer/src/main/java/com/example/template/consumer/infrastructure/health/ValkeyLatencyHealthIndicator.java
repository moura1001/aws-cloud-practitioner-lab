package com.example.template.consumer.infrastructure.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component("valkey")
public class ValkeyLatencyHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(ValkeyLatencyHealthIndicator.class);
    private static final long WARN_THRESHOLD_MS = 50;
    private static final long CRITICAL_THRESHOLD_MS = 200;
    private static final String HEALTH_DETAIL_LATENCY_MS = "latencyMs";

    private final RedisConnectionFactory connectionFactory;

    public ValkeyLatencyHealthIndicator(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Health health() {
        long start = System.nanoTime();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            String pong = connection.ping();
            long latencyMs = (System.nanoTime() - start) / 1_000_000;

            if (pong == null) {
                return Health.down()
                        .withDetail("error", "PING returned null")
                        .build();
            }

            Health.Builder builder = Health.up()
                    .withDetail(HEALTH_DETAIL_LATENCY_MS, latencyMs)
                    .withDetail("thresholds", String.format("warn=%dms,critical=%dms", WARN_THRESHOLD_MS, CRITICAL_THRESHOLD_MS));

            if (latencyMs > CRITICAL_THRESHOLD_MS) {
                log.warn("[VALKEY] Latencia critica detectada: {}ms (threshold={}ms)", latencyMs, CRITICAL_THRESHOLD_MS);
                return Health.status("DEGRADED")
                        .withDetail(HEALTH_DETAIL_LATENCY_MS, latencyMs)
                        .withDetail("thresholds", String.format("warn=%dms,critical=%dms", WARN_THRESHOLD_MS, CRITICAL_THRESHOLD_MS))
                        .build();
            }

            if (latencyMs > WARN_THRESHOLD_MS) {
                log.warn("[VALKEY] Latencia elevada: {}ms (threshold={}ms)", latencyMs, WARN_THRESHOLD_MS);
            }

            return builder.build();
        } catch (Exception e) {
            long latencyMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[VALKEY] Health check falhou apos {}ms: {}", latencyMs, e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail(HEALTH_DETAIL_LATENCY_MS, latencyMs)
                    .build();
        }
    }
}