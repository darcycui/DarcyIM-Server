package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IPrivateMessageApi
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.PrivateMessageQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.PrivateMessageSendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toDTO
import com.darcy.kotlin.server.demowebsocket.http.service.PrivateMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.RestController

@RestController
@Encrypted
class PrivateMessageController @Autowired constructor(
    val privateMessageService: PrivateMessageService,
) : IPrivateMessageApi {
    override fun sendMessage(params: PrivateMessageSendRequestDTO): PrivateMessageDTO {
        val result = privateMessageService.createMessage(
            params.senderId, params.receiverId, params.conversationId, params.content, params.msgId
        )
        return result.toDTO()
    }

    override fun queryMessagesByConversation(params: PrivateMessageQueryRequestDTO): Page<PrivateMessageDTO> {
        val result = privateMessageService.queryBothMessagesPageByConversation(
            params.conversationId, params.page, params.size)
        return result.toDTO()
    }
}