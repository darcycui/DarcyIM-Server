package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class GroupUpdateRequestDTO(
    @field:NotBlank(message = "群组ID不能为空")
    var groupId: Long = 0,

    @field:NotBlank(message = "群组名称不能为空")
    val groupName: String = ""
)
