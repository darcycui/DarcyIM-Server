package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class ReceiverMessageReadStatusMarkRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    val userId: Long = 0L,

    @field:NotBlank(message = "发送者ID不能为空")
    val fromUserName: String = "",

    @field:NotBlank(message = "接受者ID不能为空")
    val targetId: Long = 0L,

    @field:NotBlank(message = "接受者名称不能为空")
    val targetName: String = "",

    val msgIds: List<String> = listOf(),

    @field:NotBlank(message = "会话ID不能为空")
    val conversationId: Long = 1,

    @field:NotBlank(message = "会话类型不能为空")
    val conversationType: Int = 1,

    val clientType: String = "",
    val deviceId: String = ""
)
