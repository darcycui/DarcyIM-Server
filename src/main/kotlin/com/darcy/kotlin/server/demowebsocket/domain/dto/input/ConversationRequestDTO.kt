package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ConversationCreateRequestDTO(
    @field:NotNull(message = "用户ID不能为空")
    var userId: Long = 0,

    @field:NotNull(message = "目标ID不能为空")
    var targetId: Long = 0,

    @field:NotNull(message = "会话类型不能为空")
    var conversationType: Int = 0
) : IRequestDTO

data class ConversationQueryRequestDTO(
    @field:NotNull(message = "会话ID不能为空")
    var conversationId: Long = 0,
) : IRequestDTO