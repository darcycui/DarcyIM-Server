package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CommonRequestDTO(
    @field:NotNull(message = "用户ID不能为空")
    var userId: Long = 0
) : IRequestDTO
