package com.darcy.kotlin.server.demowebsocket.websocket_stomp.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.GroupMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor

interface IStomp {
    @MessageMapping("/sendPrivateMessage")
//    @SendToUser
    fun sendPrivate(sha: SimpMessageHeaderAccessor, @Payload privateMessage: PrivateMessageDTO)

    @MessageMapping("/sendAllGroupMessage")
    fun sendAllGroup(sha: SimpMessageHeaderAccessor, @Payload groupMessage: GroupMessageDTO)

    @MessageMapping("/sendTargetGroupMessage")
    fun sendTargetGroup(sha: SimpMessageHeaderAccessor, @Payload groupMessage: GroupMessageDTO)

    @MessageMapping("/markMessageRead")
    fun markMessageRead(sha: SimpMessageHeaderAccessor, @Payload receiverMessageReadStatusMarkInputDTO: ReceiverMessageReadStatusMarkInputDTO)
}