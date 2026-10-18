package com.example.template.api.infrastructure.adapter.out.messaging;

import com.example.template.api.domain.port.out.ProducerPort;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AwsSqsProducerAdapter implements ProducerPort {

    private final SqsClient sqsClient;

    @Override
    public void publish(String queueUrl, String messageBody) {
        var request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        sqsClient.sendMessage(request);
    }

    public String send(String queueUrl, String message, String correlationId) {
        Map<String, MessageAttributeValue> attributes = Map.of(
                "correlationId", MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(correlationId)
                        .build()
        );

        Span.current().setAttribute("correlationId", correlationId);

        var request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .messageAttributes(attributes)
                .build();
        return sqsClient.sendMessage(request).messageId();
    }
}