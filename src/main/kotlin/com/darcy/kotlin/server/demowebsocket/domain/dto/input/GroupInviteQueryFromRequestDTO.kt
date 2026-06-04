package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupInviteQueryFromRequestDTO(
    @field:NotBlank(message = "邀请人ID不能为空")
    var fromUserId: Long = 0,
)
