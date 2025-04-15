package com.stury.ratelimiting.common.service;

import com.stury.ratelimiting.common.annotation.RateLimit;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitKeyServiceImpl implements RateLimitKeyService {


    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, RateLimit rateLimit) {
        return buckets.computeIfAbsent(key, k -> createNewBucket(rateLimit));
    }

    public void resetBucket(String key, RateLimit rateLimit) {
        buckets.put(key, createNewBucket(rateLimit));
    }

    private Bucket createNewBucket(RateLimit rateLimit) {
        Bandwidth limit = Bandwidth.classic(
                rateLimit.capacity(),
                Refill.greedy(rateLimit.refillTokens(), Duration.ofSeconds(rateLimit.refillPeriod()))
        );
        return Bucket.builder().addLimit(limit).build();
    }

}
