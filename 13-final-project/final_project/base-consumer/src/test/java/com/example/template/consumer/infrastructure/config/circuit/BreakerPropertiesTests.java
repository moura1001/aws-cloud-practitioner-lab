package com.example.template.consumer.infrastructure.config.circuit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BreakerPropertiesTests {

    @Test
    void shouldHaveDefaultValues() {
        BreakerProperties properties = new BreakerProperties();

        assertThat(properties.getKey()).isEqualTo("microservice:external:circuit");
        assertThat(properties.getFailureThreshold()).isEqualTo(3);
        assertThat(properties.getOpenCooldownMs()).isEqualTo(30000);
        assertThat(properties.getProbeIntervalMs()).isEqualTo(10000);
        assertThat(properties.getProbeLockTtlMs()).isEqualTo(8000);
        assertThat(properties.getSyncIntervalMs()).isEqualTo(5000);
        assertThat(properties.getHalfOpenMaxSuccesses()).isEqualTo(1);
        assertThat(properties.getPauseListenerId()).isEqualTo("inputQueueListener");
    }

    @Test
    void shouldSetAndGetKey() {
        BreakerProperties properties = new BreakerProperties();
        properties.setKey("custom:key");

        assertThat(properties.getKey()).isEqualTo("custom:key");
    }

    @Test
    void shouldSetAndGetFailureThreshold() {
        BreakerProperties properties = new BreakerProperties();
        properties.setFailureThreshold(5);

        assertThat(properties.getFailureThreshold()).isEqualTo(5);
    }

    @Test
    void shouldSetAndGetOpenCooldownMs() {
        BreakerProperties properties = new BreakerProperties();
        properties.setOpenCooldownMs(60000);

        assertThat(properties.getOpenCooldownMs()).isEqualTo(60000);
    }

    @Test
    void shouldSetAndGetProbeIntervalMs() {
        BreakerProperties properties = new BreakerProperties();
        properties.setProbeIntervalMs(5000);

        assertThat(properties.getProbeIntervalMs()).isEqualTo(5000);
    }

    @Test
    void shouldSetAndGetProbeLockTtlMs() {
        BreakerProperties properties = new BreakerProperties();
        properties.setProbeLockTtlMs(4000);

        assertThat(properties.getProbeLockTtlMs()).isEqualTo(4000);
    }

    @Test
    void shouldSetAndGetSyncIntervalMs() {
        BreakerProperties properties = new BreakerProperties();
        properties.setSyncIntervalMs(10000);

        assertThat(properties.getSyncIntervalMs()).isEqualTo(10000);
    }

    @Test
    void shouldSetAndGetHalfOpenMaxSuccesses() {
        BreakerProperties properties = new BreakerProperties();
        properties.setHalfOpenMaxSuccesses(2);

        assertThat(properties.getHalfOpenMaxSuccesses()).isEqualTo(2);
    }

    @Test
    void shouldSetAndGetPauseListenerId() {
        BreakerProperties properties = new BreakerProperties();
        properties.setPauseListenerId("customListener");

        assertThat(properties.getPauseListenerId()).isEqualTo("customListener");
    }
}