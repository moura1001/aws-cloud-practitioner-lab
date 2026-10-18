package com.example.template.api.infrastructure.config.aws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aws.sdk")
public class AwsSdkProperties {
    private Http http;
    private Retry retry;
    private ApiCall apiCall;

    @Data
    public static class Http {
        private int maxConnections;
        private int connectionTimeoutMs;
        private int socketTimeoutMs;
        private int connectionAcquisitionTimeoutMs;
    }

    @Data
    public static class Retry {
        private int maxAttempts;
    }

    @Data
    public static class ApiCall {
        private int attemptTimeoutMs;
        private int timeoutMs;
    }
}