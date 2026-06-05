package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IConversationApi
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.dto.conversation.ConversationDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.conversation.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.CommonRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ConversationCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ConversationQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.http.service.ConversationService
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
@Encrypted
class ConversationController @Autowired constructor(
    private val conversationService: ConversationService,
    private val userService: UserService,
) : IConversationApi {
    override fun createConversation(params: ConversationCreateRequestDTO): ConversationDTO {
        // 调用 Service 完成业务逻辑
        val conversationType = Conversation.ConversationType.fromCode(params.conversationType)
        val result = conversationService.createConversation(
            params.userId, params.targetId, conversationType
        )
        val targetUser = userService.queryUserById(params.targetId).toDTO()
        // 返回 Object 结果
        return result.toDTO(targetUser)
    }

    override fun queryConversations(params: CommonRequestDTO): List<ConversationDTO> {
        val result = conversationService.queryConversations(params.userId)
        val targetList = result.map { item -> userService.queryUserById(item.targetId) }
        return result.toDTO(targetList)
    }

    override fun queryConversationById(params: ConversationQueryRequestDTO): ConversationDTO {
        val result = conversationService.queryOneConversation(params.conversationId)
        val targetUser = userService.queryUserById(result.targetId).toDTO()
        return result.toDTO(targetUser)
    }

}