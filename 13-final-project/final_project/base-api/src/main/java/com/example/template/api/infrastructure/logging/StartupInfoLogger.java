package com.example.template.api.infrastructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
@Profile("!local")
public class StartupInfoLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupInfoLogger.class);

    private final BuildProperties buildProperties;
    private final boolean tracingEnabled;

    public StartupInfoLogger(BuildProperties buildProperties,
                             @Value("${management.tracing.enabled:false}") boolean tracingEnabled) {
        this.buildProperties = buildProperties;
        this.tracingEnabled = tracingEnabled;
    }

    @PostConstruct
    public void logStartupInfo() {
        String version = buildProperties.getVersion();
        Instant buildTime = buildProperties.getTime();
        String buildTimeFormatted = buildTime != null
                ? DateTimeFormatter.ISO_INSTANT.format(buildTime)
                : "unknown";

        log.info("base-api v{} | Spring Boot {} | buildTime={} | tracing={}",
                version, SpringBootVersion.getVersion(), buildTimeFormatted,
                tracingEnabled ? "ENABLED" : "DISABLED");
    }
}