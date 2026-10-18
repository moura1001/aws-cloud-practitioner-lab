package com.example.template.api.infrastructure.logging.layout;

import lombok.*;

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