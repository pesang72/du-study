package com.stury.ratelimiting.test;

import com.stury.ratelimiting.common.annotation.RateLimit;
import com.stury.ratelimiting.common.exception.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @RateLimit(capacity = 5, refillTokens = 1, refillPeriod = 60)
    @GetMapping("/hello")
    public String hello() {
        return "Hello, world!";
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimit(RateLimitExceededException ex) {
        System.out.printf("RateLimitExceededException: %s\n", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
    }
}
