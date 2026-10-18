package com.example.template.api.infrastructure.adapter.out.valkey;

import com.example.template.api.domain.port.out.OperationStatusPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ValkeyOperationStatusAdapter implements OperationStatusPort {

    private static final String KEY_PREFIX = "event:status:";

    private final StringRedisTemplate redisTemplate;
    private final Duration ttl;

    public ValkeyOperationStatusAdapter(
            StringRedisTemplate redisTemplate,
            @Value("${app.redis.event-ttl:86400}") long ttlSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    @Override
    public void setStatus(String eventId, String status) {
        String key = KEY_PREFIX + eventId;
        redisTemplate.opsForValue().set(key, status, ttl);
    }

    @Override
    public String getStatus(String eventId) {
        String key = KEY_PREFIX + eventId;
        return redisTemplate.opsForValue().get(key);
    }
}