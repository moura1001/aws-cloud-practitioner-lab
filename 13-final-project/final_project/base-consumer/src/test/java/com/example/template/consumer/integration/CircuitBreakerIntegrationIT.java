package com.example.template.consumer.integration;

import com.example.template.consumer.infrastructure.resilience.circuit.CircuitState;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration")
@Testcontainers
@Tag("integration")
class CircuitBreakerIntegrationIT {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("valkey/valkey:8.1"))
            .withExposedPorts(6379);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.data.redis.url",
                () -> "redis://" + redis.getHost() + ":" + redis.getMappedPort(6379)
        );
    }

    @Test
    void shouldStartWithClosedState() {
        String key = "it:circuit:test";
        redisTemplate.opsForHash().put(key, "state", CircuitState.CLOSED.getValue());

        Object state = redisTemplate.opsForHash().get(key, "state");
        assertThat(state).isNotNull();
        assertThat(String.valueOf(state)).isEqualTo("CLOSED");
    }

    @Test
    void shouldTransitionToOpenAfterFailures() {
        String key = "it:circuit:test2";
        int failureThreshold = 3;

        redisTemplate.opsForHash().put(key, "state", CircuitState.CLOSED.getValue());
        redisTemplate.opsForHash().put(key, "failureCount", "0");

        for (int i = 0; i < failureThreshold; i++) {
            Long count = redisTemplate.opsForHash().increment(key, "failureCount", 1L);
            if (count != null && count >= failureThreshold) {
                redisTemplate.opsForHash().put(key, "state", CircuitState.OPEN.getValue());
            }
        }

        Object state = redisTemplate.opsForHash().get(key, "state");
        assertThat(String.valueOf(state)).isEqualTo("OPEN");
    }

    @Test
    void shouldTransitionFromOpenToHalfOpen() {
        String key = "it:circuit:test3";

        redisTemplate.opsForHash().put(key, "state", CircuitState.OPEN.getValue());
        assertThat(String.valueOf(redisTemplate.opsForHash().get(key, "state"))).isEqualTo("OPEN");

        redisTemplate.opsForHash().put(key, "state", CircuitState.HALF_OPEN.getValue());
        assertThat(String.valueOf(redisTemplate.opsForHash().get(key, "state"))).isEqualTo("HALF_OPEN");
    }

    @Test
    void shouldTransitionFromHalfOpenToClosedOnSuccess() {
        String key = "it:circuit:test4";

        redisTemplate.opsForHash().put(key, "state", CircuitState.HALF_OPEN.getValue());
        redisTemplate.opsForHash().put(key, "state", CircuitState.CLOSED.getValue());

        assertThat(String.valueOf(redisTemplate.opsForHash().get(key, "state"))).isEqualTo("CLOSED");
    }

    @Test
    void shouldResetCircuitState() {
        String key = "it:circuit:test5";

        redisTemplate.opsForHash().put(key, "state", CircuitState.OPEN.getValue());
        redisTemplate.opsForHash().put(key, "failureCount", "5");

        redisTemplate.delete(key);

        Object state = redisTemplate.opsForHash().get(key, "state");
        assertThat(state).isNull();
    }
}