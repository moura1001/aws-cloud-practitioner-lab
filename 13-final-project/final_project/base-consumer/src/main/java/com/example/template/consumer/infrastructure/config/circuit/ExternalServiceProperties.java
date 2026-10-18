package com.example.template.consumer.infrastructure.config.circuit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.external-service")
public class ExternalServiceProperties {
    private UrlProperties url = new UrlProperties();
    private HttpProperties http = new HttpProperties();
    private PoolProperties pool = new PoolProperties();
    private RetryProperties retry = new RetryProperties();

    @Data
    public static class UrlProperties {
        private String process;
        private String health;
    }

    @Data
    public static class HttpProperties {
        private int connectTimeoutMs = 5000;
        private int readTimeoutMs = 10000;
    }

    @Data
    public static class PoolProperties {
        private int maxConnTotal = 50;
        private int maxConnPerRoute = 20;
        private int keepAliveSeconds = 60;
    }

    @Data
    public static class RetryProperties {
        private int maxRetries = 4;
        private long initialDelayMs = 1000;
        private double multiplier = 2.0;
        private long maxDelayMs = 30000;
    }
}