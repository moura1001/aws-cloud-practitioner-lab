package com.example.template.api.infrastructure.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValkeyLatencyHealthIndicatorTests {

    @Mock
    private RedisConnectionFactory connectionFactory;

    @Mock
    private RedisConnection redisConnection;

    @Test
    void shouldReturnUpWhenLatencyIsLow() throws Exception {
        when(connectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.ping()).thenReturn("PONG");

        ValkeyLatencyHealthIndicator indicator = new ValkeyLatencyHealthIndicator(connectionFactory);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("UP");
        assertThat(health.getDetails()).containsKey("latencyMs");
    }

    @Test
    void shouldReturnDownWhenConnectionFails() throws Exception {
        when(connectionFactory.getConnection()).thenThrow(new RuntimeException("Connection refused"));

        ValkeyLatencyHealthIndicator indicator = new ValkeyLatencyHealthIndicator(connectionFactory);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
        assertThat(health.getDetails()).containsKey("error");
        assertThat((String) health.getDetails().get("error")).contains("Connection refused");
    }
}