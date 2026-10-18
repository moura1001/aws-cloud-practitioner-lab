package com.example.template.consumer.infrastructure.config.circuit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.circuit-breaker")
public class BreakerProperties {

    private String key = "microservice:external:circuit";
    private int failureThreshold = 3;
    private long openCooldownMs = 30000;
    private long probeIntervalMs = 10000;
    private long probeLockTtlMs = 8000;
    private long syncIntervalMs = 5000;
    private int halfOpenMaxSuccesses = 1;
    private String pauseListenerId = "inputQueueListener";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public long getOpenCooldownMs() {
        return openCooldownMs;
    }

    public void setOpenCooldownMs(long openCooldownMs) {
        this.openCooldownMs = openCooldownMs;
    }

    public long getProbeIntervalMs() {
        return probeIntervalMs;
    }

    public void setProbeIntervalMs(long probeIntervalMs) {
        this.probeIntervalMs = probeIntervalMs;
    }

    public long getProbeLockTtlMs() {
        return probeLockTtlMs;
    }

    public void setProbeLockTtlMs(long probeLockTtlMs) {
        this.probeLockTtlMs = probeLockTtlMs;
    }

    public long getSyncIntervalMs() {
        return syncIntervalMs;
    }

    public void setSyncIntervalMs(long syncIntervalMs) {
        this.syncIntervalMs = syncIntervalMs;
    }

    public int getHalfOpenMaxSuccesses() {
        return halfOpenMaxSuccesses;
    }

    public void setHalfOpenMaxSuccesses(int halfOpenMaxSuccesses) {
        this.halfOpenMaxSuccesses = halfOpenMaxSuccesses;
    }

    public String getPauseListenerId() {
        return pauseListenerId;
    }

    public void setPauseListenerId(String pauseListenerId) {
        this.pauseListenerId = pauseListenerId;
    }
}