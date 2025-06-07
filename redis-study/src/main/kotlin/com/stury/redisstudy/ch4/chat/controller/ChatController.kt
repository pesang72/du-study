package com.stury.redisstudy.ch4.chat.controller

import com.stury.redisstudy.ch4.chat.service.ChatUnreadService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ch4/chat")
class ChatController(
    private val chatUnreadService: ChatUnreadService
) {
    /**
     * 내가 마지막에 읽은 커서 : 100
     * 채팅방의 커서 : 150
     * 내가 안읽은건 50
     */
    @PostMapping("/{roomId}/message")
    fun addMessage(
        @PathVariable roomId: String,
        @RequestParam msgId: Long
    ) {
        chatUnreadService.addMessage(roomId, msgId)
    }

    @PostMapping("/{roomId}/read/{userId}")
    fun updateReadCursor(
        @PathVariable roomId: String,
        @PathVariable userId: String,
        @RequestParam lastReadId: Long
    ) {
        chatUnreadService.updateReadCursor(roomId, userId, lastReadId)
    }

    @GetMapping("/{roomId}/unread/{userId}")
    fun getUnreadCount(
        @PathVariable roomId: String,
        @PathVariable userId: String
    ): Long {
        return chatUnreadService.getUnreadCount(roomId, userId)
    }

    @DeleteMapping("/{roomId}/message")
    fun deleteMessage(
        @PathVariable roomId: String,
        @RequestParam msgId: Long
    ) {
        chatUnreadService.deleteMessage(roomId, msgId)
    }
}
