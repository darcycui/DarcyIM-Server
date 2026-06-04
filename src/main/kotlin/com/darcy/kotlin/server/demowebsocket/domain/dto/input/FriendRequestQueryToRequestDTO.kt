package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class FriendRequestQueryToRequestDTO(
    @field:NotBlank(message = "目标人ID不能为空")
    var toUserId: Long = 0,
)
