package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class MessageSendRequestDTO(
    @field:NotBlank(message = "发送者ID不能为空")
    var senderId: Long = 0,
    @field:NotBlank(message = "接受者ID不能为空")
    var receiverId: Long = 0,
    @field:NotBlank(message = "会话ID不能为空")
    var conversationId: Long = 0,

    @field:NotBlank(message = "消息内容不能为空")
    var content: String = "",

    @field:NotBlank(message = "消息ID不能为空")
    var msgId: String = "",
) {
}