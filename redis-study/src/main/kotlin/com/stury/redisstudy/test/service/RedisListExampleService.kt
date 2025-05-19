package com.stury.redisstudy.test.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisListExampleService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val key = "myList"

    fun lpush(vararg values: String): Long? {
        return redisTemplate.opsForList().leftPushAll(key, listOf(*values))
    }

    fun rpush(vararg values: String): Long? {
        return redisTemplate.opsForList().rightPushAll(key, listOf(*values))
    }

    fun lpop(): String? {
        return redisTemplate.opsForList().leftPop(key) as? String
    }

    fun rpop(): String? {
        return redisTemplate.opsForList().rightPop(key) as? String
    }

    fun lrange(start: Long, end: Long): List<String> {
        val result = redisTemplate.opsForList().range(key, start, end)
        return result?.mapNotNull { it as? String } ?: emptyList()
    }

    fun lindex(index: Long): String? {
        return redisTemplate.opsForList().index(key, index) as? String
    }

    fun ltrim(start: Long, end: Long) {
        redisTemplate.opsForList().trim(key, start, end)
    }
}