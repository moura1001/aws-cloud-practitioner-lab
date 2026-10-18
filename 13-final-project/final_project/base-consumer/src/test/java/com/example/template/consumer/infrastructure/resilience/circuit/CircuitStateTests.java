package com.example.template.consumer.infrastructure.resilience.circuit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CircuitStateTests {

    @Test
    void shouldFromValueReturnCorrectEnum() {
        assertThat(CircuitState.fromValue("CLOSED")).isEqualTo(CircuitState.CLOSED);
        assertThat(CircuitState.fromValue("OPEN")).isEqualTo(CircuitState.OPEN);
        assertThat(CircuitState.fromValue("HALF_OPEN")).isEqualTo(CircuitState.HALF_OPEN);
    }

    @Test
    void shouldFromValueReturnNullForUnknown() {
        assertThat(CircuitState.fromValue("INVALID")).isNull();
        assertThat(CircuitState.fromValue(null)).isNull();
        assertThat(CircuitState.fromValue("")).isNull();
    }

    @Test
    void shouldGetValueReturnCorrectString() {
        assertThat(CircuitState.CLOSED.getValue()).isEqualTo("CLOSED");
        assertThat(CircuitState.OPEN.getValue()).isEqualTo("OPEN");
        assertThat(CircuitState.HALF_OPEN.getValue()).isEqualTo("HALF_OPEN");
    }

    @Test
    void shouldHaveThreeValues() {
        assertThat(CircuitState.values()).hasSize(3);
    }

    @Test
    void shouldBeCaseSensitive() {
        assertThat(CircuitState.fromValue("closed")).isNull();
    }
}