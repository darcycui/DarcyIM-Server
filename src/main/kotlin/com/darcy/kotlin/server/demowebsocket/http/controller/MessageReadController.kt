package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IMessageReadApi
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverOfflineMessageSyncRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.SenderOfflineMessageReadSyncRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.MessageReadStatusDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toDTO
import com.darcy.kotlin.server.demowebsocket.http.service.MessageReadStatusService
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SEND_PRIVATE_MESSAGE_READ_URL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
@Encrypted
class MessageReadController @Autowired constructor(
    private val messageReadStatusService: MessageReadStatusService,
    private val websocket: SimpMessagingTemplate
) : IMessageReadApi {
    // 接收方离线消息同步
    override fun receiverPullOfflineMessages(params: ReceiverOfflineMessageSyncRequestDTO): Page<PrivateMessageDTO> {
        val result = messageReadStatusService.receiverPullOfflineMessagesByReadStatus(params)
//        val result = messageReadStatusService.receiverPullOfflineMessagesByTimestamp(input)
        return result.toDTO()
    }


    override fun receiverMarkMessagesAsRead(params: ReceiverMessageReadStatusMarkRequestDTO): MessageReadStatusDTO {
        val userId = params.userId
        val msgIds = params.msgIds
        val updatedCount = messageReadStatusService.receiverMarkMessagesAsRead(userId, msgIds)
        val result = messageReadStatusService.receiverGetMessageListReadStatus(userId, msgIds)
        val headers = mapOf(
            "fromUserId" to userId,
            "toUserId" to params.targetId,
            "url" to "/private"
        )
        // websocket 发送已读状态
        websocket.convertAndSendToUser(
            params.targetName,
            SEND_PRIVATE_MESSAGE_READ_URL,
            result.toDTO(),
            headers
        )
        return result.toDTO()
    }

    // 发送方离线已读状态同步
    override fun senderSyncOfflineMessageReadStatus(params: SenderOfflineMessageReadSyncRequestDTO): MessageReadStatusDTO {
        val result = messageReadStatusService.senderSyncOfflineMessageReadStatus(
            params.userId, params.targetId, params.since, params.until
        )
        return result.toDTO()
    }
}