package com.vivek.ratelimiter.controller;

import com.vivek.ratelimiter.service.TokenBucketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateLimiterController {

    private final TokenBucketService service;

    public RateLimiterController(TokenBucketService service) {
        this.service = service;
    }

    @GetMapping("/check")
    public String check(@RequestParam String clientId) {
        return service.isAllowed(clientId) ? "ALLOWED" : "BLOCKED";
    }
}