package com.darcy.kotlin.server.demowebsocket.websocket_stomp.controller

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.GroupMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code1000.X3DHException
import com.darcy.kotlin.server.demowebsocket.http.service.MessageReadStatusService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logI
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.api.IStomp
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SEND_PRIVATE_MESSAGE_READ_URL
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
        val sender = sha.user?.name ?: ""
        logI("单聊消息 sender: $sender message=$privateMessage")
        val dhPublicKey = sha.getFirstNativeHeader("dhPublicKey") ?: ""
        val fromUserId =
            sha.getFirstNativeHeader("fromUserId")?.toLongOrNull() ?: throw X3DHException.FROM_USER_ID_HEADER_NOT_EXIST
        val toUserId =
            sha.getFirstNativeHeader("toUserId")?.toLongOrNull() ?: throw X3DHException.TO_USER_ID_HEADER_NOT_EXIST
        val N = sha.getFirstNativeHeader("N_KEY")?.toLongOrNull()
            ?: throw X3DHException.N_KEY_HEADER_NOT_EXIST
        val PN = sha.getFirstNativeHeader("PN_KEY")?.toLongOrNull()
            ?: throw X3DHException.PN_KEY_HEADER_NOT_EXIST
        val url = sha.getFirstNativeHeader("url")
            ?: throw X3DHException.URL_HEADER_NOT_EXIST
        stompService.sendPrivate(privateMessage, fromUserId, toUserId, dhPublicKey, N, PN, url)
    }

    override fun sendAllGroup(sha: SimpMessageHeaderAccessor, @Payload groupMessage: GroupMessageDTO) {
        val sender = sha.user?.name ?: ""
        logI("全部消息 sender: $sender message=$groupMessage")
        stompService.sendAllGroup(groupMessage)
    }

    override fun sendTargetGroup(sha: SimpMessageHeaderAccessor, @Payload groupMessage: GroupMessageDTO) {
        val sender = sha.user?.name ?: ""
        logI("群消息 sender: $sender message=$groupMessage")
        stompService.sendTargetGroup(groupMessage)
    }

    override fun markMessageRead(
        sha: SimpMessageHeaderAccessor,
        receiverMessageReadStatusMarkInputDTO: ReceiverMessageReadStatusMarkRequestDTO
    ) {
        val sender = sha.user?.name ?: ""
        logI("接收方标记已读 sender: $sender message=$receiverMessageReadStatusMarkInputDTO")
        val fromUserId =
            sha.getFirstNativeHeader("fromUserId")?.toLongOrNull() ?: throw X3DHException.FROM_USER_ID_HEADER_NOT_EXIST
        val toUserId =
            sha.getFirstNativeHeader("toUserId")?.toLongOrNull() ?: throw X3DHException.TO_USER_ID_HEADER_NOT_EXIST
        val url = sha.getFirstNativeHeader("url")
            ?: throw X3DHException.URL_HEADER_NOT_EXIST
        stompService.sendMessageReadStatus(fromUserId, toUserId, url, receiverMessageReadStatusMarkInputDTO)
    }
}