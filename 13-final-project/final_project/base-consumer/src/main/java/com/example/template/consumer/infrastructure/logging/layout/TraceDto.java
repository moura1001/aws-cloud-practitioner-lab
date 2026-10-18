package com.example.template.consumer.infrastructure.logging.layout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceDto {

    private String exception;

    private String traceId;

    private String traceMsg;

}