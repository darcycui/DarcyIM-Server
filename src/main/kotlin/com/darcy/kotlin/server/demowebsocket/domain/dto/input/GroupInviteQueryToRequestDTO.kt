package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupInviteQueryToRequestDTO(
    @field:NotBlank(message = "被邀请人ID不能为空")
    var toUserId: Long = 0,
)
