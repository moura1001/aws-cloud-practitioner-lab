package com.example.template.api.infrastructure.adapter.in.rest.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.api.infrastructure.health.ApplicationLifecycle;

@RestController
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    private static final String STATUS_KEY = "status";
    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    private final ApplicationLifecycle lifecycle;

    public HealthController(ApplicationLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(STATUS_KEY, STATUS_UP);
    }

    @GetMapping("/health/live")
    public ResponseEntity<Map<String, String>> live() {
        if (lifecycle.isLive()) {
            return ResponseEntity.ok(Map.of(STATUS_KEY, STATUS_UP));
        }
        log.warn("[HEALTH] /health/live returning 503 — liveness={}", lifecycle.isLive());
        return ResponseEntity.status(503).body(Map.of(STATUS_KEY, STATUS_DOWN));
    }

    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, String>> ready() {
        if (lifecycle.isReady()) {
            return ResponseEntity.ok(Map.of(STATUS_KEY, STATUS_UP));
        }
        log.warn("[HEALTH] /health/ready returning 503 — readiness={}", lifecycle.isReady());
        return ResponseEntity.status(503).body(Map.of(STATUS_KEY, STATUS_DOWN));
    }
}