package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class FriendRequestActionRequestDTO(
    @field:NotNull(message = "好友请求ID不能为空")
    var friendRequestId: Long = 0,
) : IRequestDTO

data class FriendRequestCreateRequestDTO(
    @field:NotNull(message = "发起人ID不能为空")
    var fromUserId: Long = 0,

    @field:NotNull(message = "目标人ID不能为空")
    var toUserId: Long = 0,
) : IRequestDTO

data class FriendRequestQueryToRequestDTO(
    @field:NotNull(message = "目标人ID不能为空")
    var toUserId: Long = 0,
) : IRequestDTO

data class FriendRequestQueryFromRequestDTO(
    @field:NotNull(message = "发起人ID不能为空")
    var fromUserId: Long = 0,
) : IRequestDTO