package com.darcy.kotlin.server.demowebsocket.websocket_stomp.controller

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.GroupMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.exception.code1000.X3DHException
import com.darcy.kotlin.server.demowebsocket.http.service.MessageReadStatusService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.api.IStomp
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.service.STOMPService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class StompController @Autowired constructor(
    private val stompService: STOMPService,
    private val messageReadStatusService: MessageReadStatusService,
    private val websocket: SimpMessagingTemplate
) : IStomp {
    override fun sendPrivate(sha: SimpMessageHeaderAccessor, @Payload privateMessage: PrivateMessageDTO) {
        DarcyLogger.info("private message=$privateMessage")
        val sender = sha.user?.name ?: ""
        DarcyLogger.info("private sender: $sender message=$privateMessage")
        val dhPublicKey = sha.getFirstNativeHeader("dhPublicKey") ?: ""
        val fromUserId = sha.getFirstNativeHeader("fromUserId") ?: throw X3DHException.FROM_USER_ID_HEADER_NOT_EXIST
        val sendingIndex = sha.getFirstNativeHeader("sendingIndex")?.toLongOrNull()
            ?: throw X3DHException.SENDING_INDEX_HEADER_NOT_EXIST
        val receivingIndex = sha.getFirstNativeHeader("receivingIndex")?.toLongOrNull()
            ?: throw X3DHException.RECEIVING_INDEX_HEADER_NOT_EXIST
        stompService.sendPrivate(privateMessage, fromUserId, dhPublicKey, sendingIndex, receivingIndex)
    }

    override fun sendAllGroup(sha: SimpMessageHeaderAccessor, @Payload groupMessage: GroupMessageDTO) {
        DarcyLogger.info("all group message=$groupMessage")
        val sender = sha.user?.name ?: ""
        DarcyLogger.info("all group sender: $sender message=$groupMessage")
        stompService.sendAllGroup(groupMessage)
    }

    override fun sendTargetGroup(sha: SimpMessageHeaderAccessor, @Payload groupMessage: GroupMessageDTO) {
        DarcyLogger.info("target group message=$groupMessage")
        val sender = sha.user?.name ?: ""
        DarcyLogger.info("target group sender: $sender message=$groupMessage")
        stompService.sendTargetGroup(groupMessage)
    }

    override fun markMessageRead(sha: SimpMessageHeaderAccessor, receiverMessageReadStatusMarkInputDTO: ReceiverMessageReadStatusMarkInputDTO) {
        val userId = receiverMessageReadStatusMarkInputDTO.userId
        val msgIds = receiverMessageReadStatusMarkInputDTO.msgIds
        val updatedCount = messageReadStatusService.receiverMarkMessagesAsRead(userId, msgIds)
        val result = messageReadStatusService.receiverGetMessageListReadStatus(userId, msgIds)
        websocket.convertAndSendToUser(
            receiverMessageReadStatusMarkInputDTO.targetName,
            "/queue/message/read",
            result
        )
    }
}