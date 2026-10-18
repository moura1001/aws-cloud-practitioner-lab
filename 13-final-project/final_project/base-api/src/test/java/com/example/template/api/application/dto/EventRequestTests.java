package com.example.template.api.application.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventRequestTests {

    @Test
    void shouldBuildWithAllFields() {
        EventRequest request = EventRequest.builder()
                .id("123")
                .type("DATA")
                .data("some-data")
                .build();

        assertThat(request.getId()).isEqualTo("123");
        assertThat(request.getType()).isEqualTo("DATA");
        assertThat(request.getData()).isEqualTo("some-data");
    }

    @Test
    void shouldSetAndGetValues() {
        EventRequest request = new EventRequest();
        request.setId("456");
        request.setType("EVENT");
        request.setData("payload");

        assertThat(request.getId()).isEqualTo("456");
        assertThat(request.getType()).isEqualTo("EVENT");
        assertThat(request.getData()).isEqualTo("payload");
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        EventRequest request = new EventRequest();

        assertThat(request.getId()).isNull();
        assertThat(request.getType()).isNull();
        assertThat(request.getData()).isNull();
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        EventRequest request = new EventRequest("789", "TYPE", "content");

        assertThat(request.getId()).isEqualTo("789");
        assertThat(request.getType()).isEqualTo("TYPE");
        assertThat(request.getData()).isEqualTo("content");
    }
}