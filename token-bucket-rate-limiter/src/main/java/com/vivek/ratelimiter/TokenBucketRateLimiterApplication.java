package com.vivek.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FixedWindowRateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FixedWindowRateLimiterApplication.class, args);
	}

}
