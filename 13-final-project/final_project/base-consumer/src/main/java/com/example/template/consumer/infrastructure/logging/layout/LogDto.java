package com.example.template.consumer.infrastructure.logging.layout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogDto {

    private String correlationId;

    private String detail;

    private List<String> events;

    private QueuesDto queues;

    private String reason;

    private String requestId;

    private ResponseDto response;

    private String severity;

    private SourceDto source;

    private String timeStamp;
    private TraceDto trace;

    private String type;

}