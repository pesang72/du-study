package com.stury.redisstudy.ch4.chat.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class ChatUnreadService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private fun zSetKey(roomId: String): String = "chat:room:$roomId:messages"
    private fun readCursorKey(roomId: String, userId: String): String =
        "chat:room:$roomId:user:$userId:readCursor"

    /**
     * 메시지 추가 (새 메시지 도착)
     */
    fun addMessage(roomId: String, msgId: Long) {
        redisTemplate.opsForZSet().add(zSetKey(roomId), msgId.toString(), msgId.toDouble())
    }

    /**
     * 사용자가 읽은 마지막 메시지 업데이트
     */
    fun updateReadCursor(roomId: String, userId: String, lastReadId: Long) {
        redisTemplate.opsForValue().set(readCursorKey(roomId, userId), lastReadId.toString())
    }

    /**
     * 읽지 않은 메시지 개수 조회
     */
    fun getUnreadCount(roomId: String, userId: String): Long {

        val lastReadId = redisTemplate.opsForValue()
            .get(readCursorKey(roomId, userId))
            ?.toString()?.toLong() ?: 0L
        return redisTemplate.opsForZSet()
            .count(zSetKey(roomId), (lastReadId + 1).toDouble(), Double.MAX_VALUE) ?: 0L
    }

    /**
     * 메시지 삭제
     */
    fun deleteMessage(roomId: String, msgId: Long) {
        redisTemplate.opsForZSet().remove(zSetKey(roomId), msgId.toString())
    }
}
