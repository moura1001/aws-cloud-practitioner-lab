package com.example.template.api.infrastructure.logging.layout;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    private int status;
    private String message;
}