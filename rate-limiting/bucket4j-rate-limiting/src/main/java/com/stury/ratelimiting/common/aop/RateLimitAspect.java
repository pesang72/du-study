package com.stury.ratelimiting.common.aop;

import com.stury.ratelimiting.common.annotation.RateLimit;
import com.stury.ratelimiting.common.exception.RateLimitExceededException;
import com.stury.ratelimiting.common.service.RateLimitKeyService;
import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private final RateLimitKeyService rateLimitKeyService;

    public RateLimitAspect(RateLimitKeyService rateLimitKeyService) {
        this.rateLimitKeyService = rateLimitKeyService;
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = createKey(joinPoint);

        Bucket bucket = rateLimitKeyService.resolveBucket(key, rateLimit);

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException("Too many requests - rate limit exceeded.");
        }
    }

    private String createKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getMethod().getName();
    }
}
