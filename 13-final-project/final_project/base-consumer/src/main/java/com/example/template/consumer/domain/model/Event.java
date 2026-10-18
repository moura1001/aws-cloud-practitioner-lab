package com.example.template.consumer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String id;
    private String type;
    private String data;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}