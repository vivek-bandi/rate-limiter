package com.vivek.fixed_window_rate_limiter.controller;

import com.vivek.fixed_window_rate_limiter.service.RateLimiterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateLimiterController {

    private final RateLimiterService service;

    public RateLimiterController(RateLimiterService service) {
        this.service = service;
    }

    @GetMapping("/check")
    public String check(@RequestParam String clientId) {
        return service.isAllowed(clientId) ? "ALLOWED" : "BLOCKED";
    }
}