package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class FriendRequestQueryFromRequestDTO(
    @field:NotBlank(message = "发起人ID不能为空")
    var fromUserId: Long = 0,
)
