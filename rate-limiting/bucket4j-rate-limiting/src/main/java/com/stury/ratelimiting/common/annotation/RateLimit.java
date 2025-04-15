package com.stury.ratelimiting.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    long capacity();          // 버킷 용량
    long refillTokens();      // 리필 토큰 수
    long refillPeriod();      // 리필 주기 (초 단위)
}
