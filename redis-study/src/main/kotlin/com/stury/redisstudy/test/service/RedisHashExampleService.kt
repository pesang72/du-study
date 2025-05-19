package com.stury.redisstudy.test.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisHashExampleService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {
    fun hset(key: String, obj: Any) {
        val map = objectMapper.convertValue(obj, Map::class.java)
        redisTemplate.opsForHash<String, Any>().putAll(key, map)
    }

    fun <T> hgetall(key: String, clazz: Class<T>): T? {
        val entries = redisTemplate.opsForHash<String, Any>().entries(key)
        if (entries.isEmpty()) return null
        return objectMapper.convertValue(entries, clazz)
    }

    fun hget(key: String, field: String): Any? {
        return redisTemplate.opsForHash<String, Any>().get(key, field)
    }

    fun hdel(key: String, field: String) {
        redisTemplate.opsForHash<String, Any>().delete(key, field)
    }

    fun hexists(key: String, field: String): Boolean {
        return redisTemplate.opsForHash<String, Any>().hasKey(key, field)
    }
}
