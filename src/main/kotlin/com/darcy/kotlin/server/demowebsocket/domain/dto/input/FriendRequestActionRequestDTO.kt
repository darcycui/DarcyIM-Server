package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class FriendRequestActionRequestDTO(
    @field:NotBlank(message = "好友请求ID不能为空")
    var friendRequestId: Long = 0,
)
