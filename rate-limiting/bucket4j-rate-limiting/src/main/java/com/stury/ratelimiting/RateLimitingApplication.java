package com.stury.ratelimiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class RateLimitingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimitingApplication.class, args);
	}

}
