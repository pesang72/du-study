package com.stury.redisstudy.ch4.dailyactive.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class DailyActiveUserService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val hyperLogLogOps = redisTemplate.opsForHyperLogLog()

    /**
     * 일자별 DAU Key 생성
     * 예: dau:20250520
     */
    private fun dauKey(date: String): String = "dau:$date"

    /**
     * DAU에 사용자 추가
     */
    fun addActiveUser(date: String, userId: String) {
        hyperLogLogOps.add(dauKey(date), userId)
    }

    /**
     * DAU 조회
     */
    fun getDailyActiveUserCount(date: String): Long {
        return hyperLogLogOps.size(dauKey(date)) ?: 0L
    }
}