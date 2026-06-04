package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupMessageQueryRequestDTO(
    @field:NotBlank(message = "群组ID不能为空")
    var groupId: Long = 0,

    @field:NotBlank(message = "用户ID不能为空")
    val userId: Long = 0,

    @field:NotBlank(message = "会话ID不能为空")
    val conversationId: Long = 0,

    @field:NotBlank(message = "页码不能为空")
    var page: Int = 0,

    @field:NotBlank(message = "每页条数不能为空")
    var size: Int = 0,
) {
}