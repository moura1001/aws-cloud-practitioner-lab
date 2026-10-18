package com.example.template.consumer.infrastructure.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLifecycle {

    private static final Logger log = LoggerFactory.getLogger(ApplicationLifecycle.class);

    private final ApplicationAvailability availability;

    public ApplicationLifecycle(ApplicationAvailability availability) {
        this.availability = availability;
    }

    @EventListener
    public void onAvailabilityChange(AvailabilityChangeEvent<?> event) {
        Object state = event.getState();
        if (state instanceof ReadinessState readiness) {
            log.info("[LIFECYCLE] readiness={}", readiness);
        } else if (state instanceof LivenessState liveness) {
            log.info("[LIFECYCLE] liveness={}", liveness);
        }
    }

    public boolean isReady() {
        return availability.getReadinessState() == ReadinessState.ACCEPTING_TRAFFIC;
    }

    public boolean isLive() {
        return availability.getLivenessState() == LivenessState.CORRECT;
    }
}