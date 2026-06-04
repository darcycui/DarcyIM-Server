package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class PrivateMessageQueryRequestDTO(
    @field:NotBlank(message = "会话ID不能为空")
    val conversationId: Long = 0,

    @field:NotBlank(message = "会话类型不能为空")
    val conversationType: Int = 1,

    @field:NotBlank(message = "页码不能为空")
    var page: Int = 0,

    @field:NotBlank(message = "每页条数不能为空")
    var size: Int = 0,
) {
}