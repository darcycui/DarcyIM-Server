package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverOfflineMessageSyncRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.SenderOfflineMessageReadSyncRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.MessageReadStatusDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/message/read")
interface IMessageReadApi {
    @PostMapping("/receiver/pull/offline")
    fun receiverPullOfflineMessages(@RequestParam params: ReceiverOfflineMessageSyncRequestDTO): Page<PrivateMessageDTO>

    @PostMapping("/receiver/push/read")
    fun receiverMarkMessagesAsRead(@RequestParam params: ReceiverMessageReadStatusMarkRequestDTO): MessageReadStatusDTO

    @PostMapping("/sender/sync/offline")
    fun senderSyncOfflineMessageReadStatus(@RequestParam params: SenderOfflineMessageReadSyncRequestDTO): MessageReadStatusDTO
}
