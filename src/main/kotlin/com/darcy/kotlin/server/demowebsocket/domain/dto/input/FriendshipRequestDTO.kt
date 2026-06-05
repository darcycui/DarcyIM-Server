package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class FriendshipDeleteRequestDTO(
    @field:NotNull(message = "用户ID不能为空")
    var userId: Long = 0,

    @field:NotNull(message = "好友ID不能为空")
    var friendId: Long = 0
) : IRequestDTO

data class FriendshipUpdateRequestDTO(
    @field:NotNull(message = "好友关系ID不能为空")
    var friendshipId: Long = 0,
) : IRequestDTO
