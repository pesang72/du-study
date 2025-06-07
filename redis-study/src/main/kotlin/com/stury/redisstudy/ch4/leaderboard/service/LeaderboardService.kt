package com.stury.redisstudy.ch4.leaderboard.service

import com.stury.redisstudy.ch4.leaderboard.dto.RankResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val key = "leaderboard:game"
    private val zSetOps = redisTemplate.opsForZSet()

    // 1) 점수 생성
    fun createScore(userId: String, score: Int) {
        zSetOps.add(key, userId, score.toDouble())
    }

    // 2) 점수 업데이트 (기존 점수에 +delta)
    fun updateScore(userId: String, delta: Int) {
        zSetOps.incrementScore(key, userId, delta.toDouble())
    }

    // 3) TOP 5 조회 (내림차순)
    fun getTop5(): List<RankResponse> {
        val top5 = zSetOps.reverseRangeWithScores(key, 0, 4) ?: emptySet()
        return top5.mapIndexed { index, tuple ->
            RankResponse(
                userId = tuple.value as String,
                score = (tuple.score ?: 0.0).toInt(),
                rank = index.toLong() + 1
            )
        }
    }

    // 4) 내 앞뒤 2명까지 조회 (나를 포함해 총 5명)
    fun getAroundMe(userId: String): List<RankResponse> {
        val myRank = zSetOps.reverseRank(key, userId) ?: return emptyList()
        val start = (myRank - 2).coerceAtLeast(0)
        val end = myRank + 2
        val range = zSetOps.reverseRangeWithScores(key, start, end) ?: emptySet()

        return range.mapIndexed { index, tuple ->
            RankResponse(
                userId = tuple.value as String,
                score = (tuple.score ?: 0.0).toInt(),
                rank = start + index + 1
            )
        }
    }
}
