package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class FriendRequestCreateRequestDTO(
    @field:NotBlank(message = "发起人ID不能为空")
    var fromUserId: Long = 0,

    @field:NotBlank(message = "目标人ID不能为空")
    var toUserId: Long = 0,
)
