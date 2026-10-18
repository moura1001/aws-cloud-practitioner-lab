package com.example.template.consumer.application.service;

import com.example.template.consumer.application.dto.EventMessageDto;
import com.example.template.consumer.domain.port.in.ConsumerPort;
import com.example.template.consumer.domain.port.out.CachePort;
import com.example.template.consumer.domain.valueobject.OperationStatus;
import com.example.template.consumer.infrastructure.adapter.out.http.ExternalServiceRetryExecutor;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventProcessor implements ConsumerPort {

    private final ExternalServiceRetryExecutor externalServiceRetryExecutor;
    private final CachePort cachePort;
    private final ObjectMapper objectMapper;

    public EventProcessor(
            ExternalServiceRetryExecutor externalServiceRetryExecutor,
            CachePort cachePort,
            ObjectMapper objectMapper
    ) {
        this.externalServiceRetryExecutor = externalServiceRetryExecutor;
        this.cachePort = cachePort;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleEvent(String messageBody) {
        try {
            EventMessageDto eventMessage =
                    objectMapper.readValue(messageBody, EventMessageDto.class);

            String key = "event:" + eventMessage.getEventId();
            cachePort.put(key, OperationStatus.PROCESSING.name(), 3600L);

            String processedData = transformData(eventMessage.getData());
            externalServiceRetryExecutor.execute(processedData);

            cachePort.put(key, OperationStatus.COMPLETED.name(), 3600L);

            log.info(
                    "[PROCESSOR] Evento processado com sucesso: eventId={}",
                    eventMessage.getEventId()
            );
        } catch (Exception e) {
            log.error("[PROCESSOR] Erro ao processar evento: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar evento", e);
        }
    }

    private String transformData(String data) {
        if (data == null) {
            return "";
        }

        return data.toUpperCase();
    }
}