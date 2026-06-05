package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.IGroupMessageApi
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupMessageQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupMessageSendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.GroupMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.GroupMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.RestController

@RestController
@Encrypted
class GroupMessageController @Autowired constructor(
    private val groupMessageService: GroupMessageService
) : IGroupMessageApi {
    override fun sendMessage(params: GroupMessageSendRequestDTO): GroupMessageDTO {
        val result = groupMessageService.createMessage(
            params.senderId, params.receiverId, params.conversationId, params.content, params.msgId
        )
        return result.toDTO()
    }

    private fun validateConversationId(params: Map<String, String>): Long {
        val conversationId = params["conversationId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("conversationId" to "会话ID不能为空")
        )
        return conversationId
    }

    private fun validateGroupId(params: Map<String, String>): Long {
        val groupId = params["groupId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("groupId" to "群组ID不能为空")
        )
        return groupId
    }

    private fun validateUserId(params: Map<String, String>): Long {
        val senderId = params["userId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("userId" to "用户ID不能为空")
        )
        return senderId
    }

    override fun queryGroupMessages(params: GroupMessageQueryRequestDTO): Page<GroupMessageDTO> {
        val result = groupMessageService.queryGroupMessages(
            params.userId, params.groupId, params.conversationId, params.page, params.size
        )
        return result.toDTO()
    }
}