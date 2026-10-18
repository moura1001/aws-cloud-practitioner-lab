package com.example.template.consumer.infrastructure.config.aws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aws.sdk")
public class AwsSdkProperties {
    private HttpProperties http = new HttpProperties();
    private AsyncHttpProperties asyncHttp = new AsyncHttpProperties();
    private RetryProperties retry = new RetryProperties();
    private ApiCallProperties apiCall = new ApiCallProperties();

    @Data
    public static class HttpProperties {
        private int maxConnections = 50;
        private int connectionTimeoutMs = 2000;
        private int connectionAcquisitionTimeoutMs = 10000;
    }

    @Data
    public static class AsyncHttpProperties {
        private int maxConcurrency = 50;
        private int maxPendingConnectionAcquires = 10000;
        private int connectionTimeoutMs = 2000;
        private int connectionAcquisitionTimeoutMs = 10000;
    }

    @Data
    public static class RetryProperties {
        private int maxAttempts = 3;
    }

    @Data
    public static class ApiCallProperties {
        private int attemptTimeoutMs = 5000;
        private int timeoutMs = 30000;
    }
}