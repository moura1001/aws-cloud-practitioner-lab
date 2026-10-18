package com.example.template.consumer.infrastructure.config.aws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "aws.sqs")
public class SqsProperties {
    private Map<String, String> queues = new HashMap<>();
    private int visibilityTimeoutSeconds = 90;
    private int maxMessagesPerPoll = 10;

    @Data
    public static class Queues {
        private String inputQueue;
        private String dlqQueue;
    }
}