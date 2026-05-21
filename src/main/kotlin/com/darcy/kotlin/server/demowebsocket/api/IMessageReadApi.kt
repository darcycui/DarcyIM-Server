package com.darcy.kotlin.server.demowebsocket.api

import org.springframework.web.bind.annotation.*

@RequestMapping("/api/message/read")
interface IMessageReadApi {
    @PostMapping("/mark_read")
    fun markMessageRead(@RequestParam params: Map<String, String>): String

    @PostMapping("/query/unread_count")
    fun getUnreadCount(@RequestParam params: Map<String, String>): String
}
