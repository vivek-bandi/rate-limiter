package com.vivek.ratelimiter.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class TokenBucketService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    public TokenBucketService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("scripts/token_bucket.lua"));
        this.script.setResultType(Long.class);
    }

    public boolean isAllowed(String clientId) {
        String key = "rate_limit:" + clientId;

        long now = System.currentTimeMillis();

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                "5",   // capacity
                "1",   // refill rate (tokens per second)
                String.valueOf(now)
        );

        return result != null && result == 1;
    }
}