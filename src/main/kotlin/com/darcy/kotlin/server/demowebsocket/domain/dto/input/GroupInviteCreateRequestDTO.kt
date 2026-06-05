package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class GroupInviteCreateRequestDTO(
    @field:NotNull(message = "群组ID不能为空")
    var groupId: Long = 0,

    @field:NotNull(message = "发起用户ID不能为空")
    var inviterId: Long = 0,

    @field:NotNull(message = "被邀请用户ID不能为空")
    var inviteeId: Long = 0
) : IRequestDTO

data class GroupInviteQueryToRequestDTO(
    @field:NotNull(message = "被邀请人ID不能为空")
    var toUserId: Long = 0,
) : IRequestDTO

data class GroupInviteQueryFromRequestDTO(
    @field:NotNull(message = "邀请人ID不能为空")
    var fromUserId: Long = 0,
) : IRequestDTO