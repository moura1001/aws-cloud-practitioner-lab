package com.example.template.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqsMessageDto {

    private String eventId;
    private String type;
    private String data;
    private String timestamp;
}