package com.example.template.consumer.infrastructure.resilience.circuit;

import java.util.Arrays;

public enum CircuitState {
    CLOSED("CLOSED"),
    OPEN("OPEN"),
    HALF_OPEN("HALF_OPEN");

    private final String value;

    CircuitState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CircuitState fromValue(String value) {
        return Arrays.stream(values())
                .filter(s -> s.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}