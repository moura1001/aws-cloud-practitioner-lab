package com.example.template.api.infrastructure.config.aws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aws.sqs")
public class SqsProperties {
    private Queues queues;

    @Data
    public static class Queues {
        private String inputQueue;
    }
}