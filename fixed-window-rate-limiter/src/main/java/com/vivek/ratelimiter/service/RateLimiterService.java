package com.vivek.ratelimiter.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("scripts/fixed_window.lua"));
        this.script.setResultType(Long.class);
    }

    public boolean isAllowed(String clientId) {
        String key = "rate_limit:" + clientId;

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                "5",   // max requests
                "10"   // seconds
        );

        return result != null && result == 1;
    }
}