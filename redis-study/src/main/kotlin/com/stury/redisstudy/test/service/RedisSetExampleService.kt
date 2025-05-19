package com.stury.redisstudy.test.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisSetExampleService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun sadd(key: String, vararg values: String): Long {
        return redisTemplate.opsForSet().add(key, *values) ?: 0
    }

    fun srem(key: String, vararg values: String): Long {
        return redisTemplate.opsForSet().remove(key, *values) ?: 0
    }

    fun smembers(key: String): Set<String> {
        val result = redisTemplate.opsForSet().members(key)
        return result?.mapNotNull { it as? String }?.toSet() ?: emptySet()
    }

    fun sismember(key: String, value: String): Boolean {
        return redisTemplate.opsForSet().isMember(key, value) == true
    }

    fun scard(key: String): Long {
        return redisTemplate.opsForSet().size(key) ?: 0
    }

    fun sunion(key1: String, key2: String): Set<String> {
        val result = redisTemplate.opsForSet().union(key1, key2)
        return result?.mapNotNull { it as? String }?.toSet() ?: emptySet()
    }

    fun sinter(key1: String, key2: String): Set<String> {
        val result = redisTemplate.opsForSet().intersect(key1, key2)
        return result?.mapNotNull { it as? String }?.toSet() ?: emptySet()
    }

    fun sdiff(key1: String, key2: String): Set<String> {
        val result = redisTemplate.opsForSet().difference(key1, key2)
        return result?.mapNotNull { it as? String }?.toSet() ?: emptySet()
    }
}
