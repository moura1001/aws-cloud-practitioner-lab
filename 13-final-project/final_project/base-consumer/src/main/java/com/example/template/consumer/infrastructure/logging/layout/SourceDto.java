package com.example.template.consumer.infrastructure.logging.layout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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