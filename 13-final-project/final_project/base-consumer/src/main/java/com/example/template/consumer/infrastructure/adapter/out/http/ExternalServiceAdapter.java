package com.example.template.consumer.infrastructure.adapter.out.http;

import com.example.template.consumer.domain.port.out.ExternalServicePort;
import com.example.template.consumer.infrastructure.config.circuit.ExternalServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalServiceAdapter implements ExternalServicePort {

    private static final Logger log = LoggerFactory.getLogger(ExternalServiceAdapter.class);

    private final RestTemplate externalServiceRestTemplate;
    private final ExternalServiceProperties properties;

    public ExternalServiceAdapter(RestTemplate externalServiceRestTemplate,
                                   ExternalServiceProperties properties) {
        this.externalServiceRestTemplate = externalServiceRestTemplate;
        this.properties = properties;
    }

    @Override
    public String process(String payload) throws ExternalServiceException {
        log.info("[EXTERNAL] Calling external service with payload length={}", payload.length());
        try {
            var response = externalServiceRestTemplate.postForEntity(
                    properties.getUrl().getProcess(),
                    payload,
                    String.class
            );
            log.info("[EXTERNAL] Response status={}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("[EXTERNAL] Failed to call external service: {}", e.getMessage());
            throw new ExternalServiceException("External service call failed", e);
        }
    }

    public void probeHealth() {
        String healthUrl = properties.getUrl().getHealth();
        if (healthUrl == null || healthUrl.isBlank()) {
            throw new UnsupportedOperationException("External service health endpoint not configured");
        }

        try {
            var response = externalServiceRestTemplate.getForEntity(
                    healthUrl,
                    String.class
            );

            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new IllegalStateException(
                        "External service health check expected HTTP 200 OK, but received "
                                + response.getStatusCode()
                );
            }

            log.info("[EXTERNAL][HEALTH] External service available. url={}", healthUrl);
        } catch (Exception ex) {
            log.warn("[EXTERNAL][HEALTH] External service unavailable. url={} error={}",
                    healthUrl, ex.getMessage());
            throw ex;
        }
    }
}