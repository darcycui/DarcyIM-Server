package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupInviteRequestDTO(
    @field:NotBlank(message = "群组ID不能为空")
    var groupId: Long = 0,

    @field:NotBlank(message = "发起用户ID不能为空")
    var inviterId: Long = 0,

    @field:NotBlank(message = "被邀请用户ID不能为空")
    var inviteeId: Long = 0
)
