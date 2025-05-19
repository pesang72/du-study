package com.stury.redisstudy.test.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisZSetExampleService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun zadd(key: String, member: String, score: Double): Boolean {
        return redisTemplate.opsForZSet().add(key, member, score) ?: false
    }

    fun zrange(key: String, start: Long, end: Long): Set<String> {
        val result = redisTemplate.opsForZSet().range(key, start, end)
        return result?.mapNotNull { it as? String }?.toSet() ?: emptySet()
    }

    fun zrevrange(key: String, start: Long, end: Long): Set<String> {
        val result = redisTemplate.opsForZSet().reverseRange(key, start, end)
        return result?.mapNotNull { it as? String }?.toSet() ?: emptySet()
    }

    fun zrank(key: String, member: String): Long? {
        return redisTemplate.opsForZSet().rank(key, member)
    }

    fun zscore(key: String, member: String): Double? {
        return redisTemplate.opsForZSet().score(key, member)
    }

    fun zrem(key: String, member: String): Long {
        return redisTemplate.opsForZSet().remove(key, member) ?: 0
    }

    fun zcard(key: String): Long {
        return redisTemplate.opsForZSet().size(key) ?: 0
    }
}
