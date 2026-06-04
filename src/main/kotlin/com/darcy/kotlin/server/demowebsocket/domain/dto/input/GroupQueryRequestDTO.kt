package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupQueryRequestDTO(
    @field:NotBlank(message = "群组ID不能为空")
    var groupId: Long = 0,
)
