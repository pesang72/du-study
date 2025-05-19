package com.stury.redisstudy.test.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisStringExampleService(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun setHelloWorld() {
        redisTemplate.opsForValue().set("hello", "world")
    }

    fun getHello(): String? {
        val result = redisTemplate.opsForValue().get("hello")
        return result as? String
    }

    fun mset(values: Map<String, Any>) {
        redisTemplate.opsForValue().multiSet(values)
    }

    fun mget(keys: List<String>): List<Any?> {
        return redisTemplate.opsForValue().multiGet(keys) ?: emptyList()
    }
}