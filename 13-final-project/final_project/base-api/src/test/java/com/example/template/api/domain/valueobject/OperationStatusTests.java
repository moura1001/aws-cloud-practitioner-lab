package com.example.template.api.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationStatusTests {

    @Test
    void shouldHaveEnumValues() {
        assertThat(OperationStatus.values()).containsExactly(
                OperationStatus.PENDING,
                OperationStatus.PROCESSING,
                OperationStatus.COMPLETED,
                OperationStatus.FAILED
        );
    }

    @Test
    void shouldReturnCorrectName() {
        assertThat(OperationStatus.PENDING.name()).isEqualTo("PENDING");
        assertThat(OperationStatus.PROCESSING.name()).isEqualTo("PROCESSING");
        assertThat(OperationStatus.COMPLETED.name()).isEqualTo("COMPLETED");
        assertThat(OperationStatus.FAILED.name()).isEqualTo("FAILED");
    }

    @Test
    void shouldResolveValueOf() {
        assertThat(OperationStatus.valueOf("PENDING")).isEqualTo(OperationStatus.PENDING);
        assertThat(OperationStatus.valueOf("PROCESSING")).isEqualTo(OperationStatus.PROCESSING);
        assertThat(OperationStatus.valueOf("COMPLETED")).isEqualTo(OperationStatus.COMPLETED);
        assertThat(OperationStatus.valueOf("FAILED")).isEqualTo(OperationStatus.FAILED);
    }
}