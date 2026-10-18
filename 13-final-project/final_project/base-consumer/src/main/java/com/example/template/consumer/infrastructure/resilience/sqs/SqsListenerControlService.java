package com.example.template.consumer.infrastructure.resilience.sqs;

import io.awspring.cloud.sqs.listener.MessageListenerContainer;
import io.awspring.cloud.sqs.listener.MessageListenerContainerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqsListenerControlService {

    private static final Logger log = LoggerFactory.getLogger(SqsListenerControlService.class);
    private static final String ENTRADA_LISTENER_ID = "inputQueueListener";
    private static final String DLQ_LISTENER_ID = "dlqQueueListener";

    private final MessageListenerContainerRegistry registry;

    public SqsListenerControlService(MessageListenerContainerRegistry registry) {
        this.registry = registry;
    }

    public void pauseInputQueue(String reason) {
        pauseListener(ENTRADA_LISTENER_ID, reason);
    }

    public void resumeInputQueue(String reason) {
        resumeListener(ENTRADA_LISTENER_ID, reason);
    }

    public void pauseDlqQueue(String reason) {
        pauseListener(DLQ_LISTENER_ID, reason);
    }

    public void resumeDlqQueue(String reason) {
        resumeListener(DLQ_LISTENER_ID, reason);
    }

    public void pauseAllInputListeners(String reason) {
        pauseListener(ENTRADA_LISTENER_ID, reason);
        pauseListener(DLQ_LISTENER_ID, reason);
    }

    public void resumeAllInputListeners(String reason) {
        resumeListener(ENTRADA_LISTENER_ID, reason);
        resumeListener(DLQ_LISTENER_ID, reason);
    }

    private void pauseListener(String listenerId, String reason) {
        MessageListenerContainer<?> container = registry.getContainerById(listenerId);
        if (container == null) {
            log.warn("[SQS][{}] Listener not found. Cannot pause. reason={}", listenerId, reason);
            return;
        }

        log.info("[SQS][{}] Pausing listener. reason={} runningBefore={}", listenerId, reason, container.isRunning());
        container.stop();
        log.info("[SQS][{}] Listener paused. runningAfter={}", listenerId, container.isRunning());
    }

    private void resumeListener(String listenerId, String reason) {
        MessageListenerContainer<?> container = registry.getContainerById(listenerId);
        if (container == null) {
            log.warn("[SQS][{}] Listener not found. Cannot resume. reason={}", listenerId, reason);
            return;
        }

        log.info("[SQS][{}] Resuming listener. reason={} runningBefore={}", listenerId, reason, container.isRunning());
        container.start();
        log.info("[SQS][{}] Listener resumed. runningAfter={}", listenerId, container.isRunning());
    }
}