package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupInviteCreateRequestDTO(
    @field:NotBlank(message = "群组ID不能为空")
    var groupId: Long = 0,

    @field:NotBlank(message = "发起用户ID不能为空")
    var inviterId: Long = 0,

    @field:NotBlank(message = "被邀请用户ID不能为空")
    var inviteeId: Long = 0
) : IRequestDTO

data class GroupInviteQueryToRequestDTO(
    @field:NotBlank(message = "被邀请人ID不能为空")
    var toUserId: Long = 0,
) : IRequestDTO

data class GroupInviteQueryFromRequestDTO(
    @field:NotBlank(message = "邀请人ID不能为空")
    var fromUserId: Long = 0,
) : IRequestDTO