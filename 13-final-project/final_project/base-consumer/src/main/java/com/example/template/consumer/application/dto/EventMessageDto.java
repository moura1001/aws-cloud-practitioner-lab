package com.example.template.consumer.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessageDto {
    private String eventId;
    private String type;
    private String data;
    private String timestamp;
}