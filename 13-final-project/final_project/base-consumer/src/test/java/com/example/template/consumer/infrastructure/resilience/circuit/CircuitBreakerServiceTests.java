package com.example.template.consumer.infrastructure.resilience.circuit;

import com.example.template.consumer.infrastructure.config.circuit.BreakerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CircuitBreakerServiceTests {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    private BreakerProperties properties;
    private CircuitBreakerService service;

    @BeforeEach
    void setUp() {
        properties = new BreakerProperties();
        properties.setKey("test:circuit");
        properties.setFailureThreshold(3);
        properties.setOpenCooldownMs(1000L);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        service = new CircuitBreakerService(redisTemplate, properties);
    }

    @Test
    void isOpen() {
        when(hashOperations.get("test:circuit", "state")).thenReturn("OPEN");

        assertThat(service.isOpen()).isTrue();
        assertThat(service.isClosed()).isFalse();
        assertThat(service.isHalfOpen()).isFalse();
    }

    @Test
    void isHalfOpen() {
        when(hashOperations.get("test:circuit", "state")).thenReturn("HALF_OPEN");

        assertThat(service.isHalfOpen()).isTrue();
        assertThat(service.isOpen()).isFalse();
        assertThat(service.isClosed()).isFalse();
    }

    @Test
    void isClosed() {
        when(hashOperations.get("test:circuit", "state")).thenReturn("CLOSED");

        assertThat(service.isClosed()).isTrue();
        assertThat(service.isOpen()).isFalse();
        assertThat(service.isHalfOpen()).isFalse();
    }

    @Test
    void isClosedWhenStateIsNull() {
        when(hashOperations.get("test:circuit", "state")).thenReturn(null);

        assertThat(service.isClosed()).isTrue();
    }

    @Test
    void markSuccess() {
        service.markSuccess();

        verify(hashOperations).putAll(eq("test:circuit"), any());
    }

    @Test
    void markFailureOpensAfterThreshold() {
        when(hashOperations.increment(eq("test:circuit"), eq("failureCount"), eq(1L))).thenReturn(3L);

        service.markFailure("Connection timeout");

        verify(hashOperations, atLeastOnce()).putAll(eq("test:circuit"), any());
    }

    @Test
    void reset() {
        service.reset();

        verify(redisTemplate).delete("test:circuit");
    }
}