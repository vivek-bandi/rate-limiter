package com.vivek.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TokenBucketRateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokenBucketRateLimiterApplication.class, args);
	}

}
