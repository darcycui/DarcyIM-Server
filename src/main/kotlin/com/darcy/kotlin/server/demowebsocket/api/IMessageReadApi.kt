package com.darcy.kotlin.server.demowebsocket.api

import org.springframework.web.bind.annotation.*

@RequestMapping("/api/message/read")
interface IMessageReadApi {
    @PostMapping("/receiver/pull/offline")
    fun receiverPullOfflineMessages(@RequestParam params: Map<String, String>): String

    @PostMapping("/receiver/push/read")
    fun receiverMarkMessagesAsRead(@RequestParam params: Map<String, String>): String

    @PostMapping("/sender/sync/offline")
    fun senderSyncOfflineMessageStatus(@RequestParam params: Map<String, String>): String
}
