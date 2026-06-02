package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IConversationApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.conversation.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.ConversationService
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class ConversationController @Autowired constructor(
    private val conversationService: ConversationService,
    private val userService: UserService,
) : IConversationApi {
    override fun createConversation(params: Map<String, String>): String {
        // http 参数校验
        val userId = params["userId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("userId" to "用户ID不能为空")
        )
        val targetId = params["targetId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("targetId" to "目标用户ID不能为空")
        )
        val conversationType = params["conversationType"]?.toIntOrNull()?.let {
            Conversation.ConversationType.fromCode(it)
        } ?: throw ParamsException.ParamsNotValid(
            mapOf("conversationType" to "会话类型不能为空")
        )
        // 调用 Service 完成业务逻辑
        val result = conversationService.createConversation(userId, conversationType, targetId)
        val targetUser = userService.queryUserById(targetId).toDTO()
        // 返回 json结果
        return ResultEntity.success(result.toDTO(targetUser)).toJsonString()
    }

    override fun queryConversations(params: Map<String, String>): String {
        val userId = params["userId"]?.toLongOrNull() ?: 0L
        if (userId == 0L) {
            throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        }
        val result = conversationService.queryConversations(userId)
        val targetList = result.map { item -> userService.queryUserById(item.targetId) }
        return ResultEntity.success(result.toDTO(targetList)).toJsonString()
    }

    override fun queryConversationById(params: Map<String, String>): String {
        val conversationId = params["conversationId"]?.toLongOrNull() ?: 0L
        if (conversationId == 0L) {
            throw ParamsException.ParamsNotValid(mapOf("conversationId" to "会话ID不能为空"))
        }
        val result = conversationService.queryOneConversation(conversationId)
        val targetUser = userService.queryUserById(result.targetId).toDTO()
        return ResultEntity.success(result.toDTO(targetUser)).toJsonString()
    }

}