package com.example.template.consumer.infrastructure.adapter.out.http;

import com.example.template.consumer.infrastructure.config.circuit.ExternalServiceProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ExternalServiceAdapterTests {

    private RestTemplate restTemplate;
    private ExternalServiceProperties properties;
    private ExternalServiceAdapter adapter;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);

        properties = new ExternalServiceProperties();
        properties.getUrl().setHealth("http://localhost:8888/health");

        adapter = new ExternalServiceAdapter(restTemplate, properties);
    }

    @Test
    void shouldAcceptHealthCheckWhenStatusIs200() {
        when(restTemplate.getForEntity(
                "http://localhost:8888/health",
                String.class
        )).thenReturn(ResponseEntity.ok("healthy"));

        assertThatCode(() -> adapter.probeHealth())
                .doesNotThrowAnyException();

        verify(restTemplate).getForEntity(
                "http://localhost:8888/health",
                String.class
        );
    }

    @Test
    void shouldRejectHealthCheckWhenStatusIs204() {
        when(restTemplate.getForEntity(
                "http://localhost:8888/health",
                String.class
        )).thenReturn(
                ResponseEntity.<String>status(HttpStatus.NO_CONTENT).build()
        );

        assertThatThrownBy(() -> adapter.probeHealth())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expected HTTP 200 OK");

        verify(restTemplate).getForEntity(
                "http://localhost:8888/health",
                String.class
        );
    }

    @Test
    void shouldRejectHealthCheckWhenUrlIsNotConfigured() {
        properties.getUrl().setHealth("");
        
        assertThatThrownBy(() -> adapter.probeHealth())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("health endpoint not configured");

        verifyNoInteractions(restTemplate);
    }
}
