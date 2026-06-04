package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupCreateRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    var ownerId: Long = 0,

    @field:NotBlank(message = "群组名称不能为空")
    val groupName: String = ""
)
