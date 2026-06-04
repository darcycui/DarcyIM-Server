package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class ConversationQueryRequestDTO(
    @field:NotBlank(message = "会话ID不能为空")
    var conversationId: Long = 0,
)
