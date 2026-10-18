package com.example.template.api.infrastructure.logging.layout;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourceDto {
    private String clientIp;
    private String context;
    private String entity;
    private String function;
    private String process;
}