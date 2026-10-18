package com.example.template.api.infrastructure.adapter.in.rest.controllers;

import com.example.template.api.domain.exception.InvalidPayloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String RESPONSE_STATUS_FAILED = "FAILED";
    private static final String RESPONSE_KEY_ERROR = "error";
    private static final String RESPONSE_KEY_STATUS = "status";

    @ExceptionHandler(InvalidPayloadException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPayload(InvalidPayloadException ex) {
        log.warn("[API] Payload inválido: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        RESPONSE_KEY_STATUS, RESPONSE_STATUS_FAILED,
                        RESPONSE_KEY_ERROR, ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        log.error("[API] Erro ao publicar mensagem: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        RESPONSE_KEY_STATUS, RESPONSE_STATUS_FAILED,
                        RESPONSE_KEY_ERROR, "Falha ao publicar mensagem na fila"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("[API] Erro inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        RESPONSE_KEY_STATUS, RESPONSE_STATUS_FAILED,
                        RESPONSE_KEY_ERROR, "Erro interno inesperado"
                ));
    }
}