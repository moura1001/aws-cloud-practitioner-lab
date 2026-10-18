package com.example.template.consumer.domain.port.out;

public interface CachePort {
    void put(String key, String value, long ttlSeconds);

    String get(String key);

    void delete(String key);
}