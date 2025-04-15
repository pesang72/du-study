package com.stury.ratelimiting.common.service;

import com.stury.ratelimiting.common.annotation.RateLimit;
import io.github.bucket4j.Bucket;

public interface RateLimitKeyService {
    Bucket resolveBucket(String key, RateLimit rateLimit);

    void resetBucket(String key, RateLimit rateLimit);
}
