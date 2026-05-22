package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.IGroupMessageApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.GroupMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupMessageController @Autowired constructor(
    private val groupMessageService: GroupMessageService
) : IGroupMessageApi {
    override fun sendMessage(params: Map<String, String>): String {
        val senderId = validateUserId(params)
        val groupId = validateGroupId(params)
        val conversationId = validateConversationId(params)
        val content = params["content"] ?: throw ParamsException.ParamsNotValid(
            mapOf("content" to "消息内容不能为空")
        )
        val msgId = params["msgId"] ?: throw ParamsException.ParamsNotValid(
            mapOf("msgId" to "消息ID不能为空")
        )
        val result = groupMessageService.createMessage(senderId, groupId, conversationId, content, msgId)
        return ResultEntity.success(result.toDTO()).toJsonString()
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

    override fun queryGroupMessages(params: Map<String, String>): String {
        val userId = validateUserId(params)
        val groupId = validateGroupId(params)
        val conversationId = validateConversationId(params)
        val page = params["page"]?.toIntOrNull() ?: 0
        val size = params["size"]?.toIntOrNull() ?: 2
        val result = groupMessageService.queryGroupMessages(userId, groupId, conversationId, page, size)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }
}