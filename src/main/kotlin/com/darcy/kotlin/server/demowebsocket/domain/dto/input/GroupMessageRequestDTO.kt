package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class GroupMessageQueryRequestDTO(
    @field:NotNull(message = "群组ID不能为空")
    var groupId: Long = 0,

    @field:NotNull(message = "用户ID不能为空")
    val userId: Long = 0,

    @field:NotNull(message = "会话ID不能为空")
    val conversationId: Long = 0,

    @field:NotNull(message = "页码不能为空")
    var page: Int = 0,

    @field:NotNull(message = "每页条数不能为空")
    var size: Int = 0,
) : IRequestDTO {
}

data class GroupMessageSendRequestDTO(
    @field:NotNull(message = "发送者ID不能为空")
    var senderId: Long = 0,
    @field:NotNull(message = "接受者ID不能为空")
    var receiverId: Long = 0,
    @field:NotNull(message = "会话ID不能为空")
    var conversationId: Long = 0,

    @field:NotBlank(message = "消息内容不能为空")
    var content: String = "",

    @field:NotBlank(message = "消息ID不能为空")
    var msgId: String = "",
) : IRequestDTO {
}