package com.example.template.consumer.domain.exception;

public class MissingCorrelationIdException extends RuntimeException {
    private final String redisKey;

    public MissingCorrelationIdException(String redisKey) {
        super("correlationId nao encontrado no Redis para a chave: " + redisKey);
        this.redisKey = redisKey;
    }

    public String getRedisKey() {
        return redisKey;
    }
}