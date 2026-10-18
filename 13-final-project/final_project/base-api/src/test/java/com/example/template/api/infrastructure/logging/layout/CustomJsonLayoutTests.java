package com.example.template.api.infrastructure.logging.layout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomJsonLayoutTests {

    private final CustomJsonLayout layout = new CustomJsonLayout();

    @Test
    void shouldSerializeLogEventToJson() {
        Map<String, String> mdcMap = new HashMap<>();
        mdcMap.put("correlationId", "corr-123");
        mdcMap.put("traceId", "trace-456");

        long timestamp = System.currentTimeMillis();

        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        when(event.getTimeStamp()).thenReturn(timestamp);
        when(event.getLevel()).thenReturn(Level.INFO);
        when(event.getFormattedMessage()).thenReturn("Test log message");
        when(event.getThrowableProxy()).thenReturn(null);

        String result = layout.doLayout(event);

        assertThat(result).isNotNull();
        assertThat(result).isNotBlank();
        assertThat(result).contains("\"severity\":\"INFO\"");
        assertThat(result).contains("\"correlationId\":\"corr-123\"");
        assertThat(result).contains("\"traceId\":\"trace-456\"");
        assertThat(result).contains("\"detail\":\"Test log message\"");
        assertThat(result).contains("\"type\":\"\"");
        assertThat(result).contains("\"source\"");
        assertThat(result).contains("\"response\"");
    }

    @Test
    void shouldHandleEmptyMdcMap() {
        Map<String, String> mdcMap = new HashMap<>();

        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        when(event.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(event.getLevel()).thenReturn(Level.WARN);
        when(event.getFormattedMessage()).thenReturn("Warning message");
        when(event.getThrowableProxy()).thenReturn(null);

        String result = layout.doLayout(event);

        assertThat(result).contains("\"severity\":\"WARN\"");
        assertThat(result).contains("\"correlationId\":\"\"");
        assertThat(result).contains("\"traceId\":\"\"");
    }

    @Test
    void shouldIncludeTraceInfoWhenExceptionPresent() {
        Map<String, String> mdcMap = new HashMap<>();

        IThrowableProxy throwableProxy = mock(IThrowableProxy.class);
        when(throwableProxy.getClassName()).thenReturn("java.lang.RuntimeException");
        when(throwableProxy.getMessage()).thenReturn("Test error");
        when(throwableProxy.getStackTraceElementProxyArray()).thenReturn(new ch.qos.logback.classic.spi.StackTraceElementProxy[0]);

        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        when(event.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(event.getLevel()).thenReturn(Level.ERROR);
        when(event.getFormattedMessage()).thenReturn("Error occurred");
        when(event.getThrowableProxy()).thenReturn(throwableProxy);

        String result = layout.doLayout(event);

        assertThat(result).contains("\"severity\":\"ERROR\"");
        assertThat(result).contains("\"exception\":\"java.lang.RuntimeException: Test error\"");
        assertThat(result).contains("\"traceMsg\"");
    }
}