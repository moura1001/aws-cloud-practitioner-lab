package com.example.template.consumer.infrastructure.logging.layout;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.status.Status;
import com.example.template.consumer.infrastructure.logging.CorrelationIdSupport;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomJsonLayout implements Layout<ILoggingEvent> {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private Context context;
    private boolean started;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    @Override
    public String doLayout(ILoggingEvent event) {
        Map<String, Object> map = new LinkedHashMap<>();
        addCustomDataToJsonMap(map, event);
        try {
            return jsonMapper.writeValueAsString(map) + System.lineSeparator();
        } catch (JacksonException e) {
            return "{\"error\": \"Failed to serialize log\"}" + System.lineSeparator();
        }
    }

    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        LogDto log = new LogDto();
        Map<String, String> mdc = event.getMDCPropertyMap();
        String correlationId = mdc.get(CorrelationIdSupport.CORRELATION_ID_KEY);
        String traceId = mdc.get("traceId");
        log.setCorrelationId(correlationId);

        map.put("timestamp", FORMATTER.format(Instant.ofEpochMilli(event.getTimeStamp())));
        map.put("severity", event.getLevel().levelStr);
        map.put("traceId", traceId != null ? traceId : "");
        map.put("correlationId", correlationId != null ? correlationId : "");
        map.put("correlation_id", log.getCorrelationId());
        map.put("detail", event.getFormattedMessage());
        map.put("events", log.getEvents() != null ? log.getEvents() : new ArrayList<>());
        map.put("queues", log.getQueues() != null ? log.getQueues() : new LinkedHashMap<>());
        map.put("reason", log.getReason() != null ? log.getReason() : "");
        map.put("request_id", log.getRequestId() != null ? log.getRequestId() : UUID.randomUUID().toString());
        map.put("response", log.getResponse() != null ? log.getResponse() : ResponseDto.builder().build());
        map.put("source", log.getSource() != null ? log.getSource() : SourceDto.builder().clientIp("").context("BaseMicroservice").entity("").function("").process("").build());
        map.put("type", log.getType() != null ? log.getType() : "");

        if (event.getThrowableProxy() != null) {
            map.put("trace", buildTraceDto(event.getThrowableProxy()));
        } else {
            map.put("trace", TraceDto.builder()
                    .exception("No exception")
                    .traceId("No Trace ID")
                    .traceMsg("No stack trace")
                    .build());
        }
    }

    private static final int MAX_STACK_TRACE_FRAMES = 50;

    private TraceDto buildTraceDto(IThrowableProxy throwableProxy) {
        String stackTrace = Arrays.stream(throwableProxy.getStackTraceElementProxyArray())
                .limit(MAX_STACK_TRACE_FRAMES)
                .map(StackTraceElementProxy::getSTEAsString)
                .collect(Collectors.joining("\n"));

        String suffix = throwableProxy.getStackTraceElementProxyArray().length > MAX_STACK_TRACE_FRAMES
                ? "\n... (" + (throwableProxy.getStackTraceElementProxyArray().length - MAX_STACK_TRACE_FRAMES) + " more frames truncated)"
                : "";

        return TraceDto.builder()
                .exception(String.format("%s: %s", throwableProxy.getClassName(), throwableProxy.getMessage()))
                .traceMsg(stackTrace + suffix)
                .build();
    }


    @Override
    public String getFileHeader() {
        return "";
    }

    @Override
    public String getPresentationHeader() {
        return "";
    }

    @Override
    public String getPresentationFooter() {
        return "";
    }

    @Override
    public String getFileFooter() {
        return "";
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void addStatus(Status status) {
    }

    @Override
    public void addInfo(String s) {
    }

    @Override
    public void addInfo(String s, Throwable throwable) {
    }

    @Override
    public void addWarn(String s) {
    }

    @Override
    public void addWarn(String s, Throwable throwable) {
    }

    @Override
    public void addError(String s) {
    }

    @Override
    public void addError(String s, Throwable throwable) {
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}