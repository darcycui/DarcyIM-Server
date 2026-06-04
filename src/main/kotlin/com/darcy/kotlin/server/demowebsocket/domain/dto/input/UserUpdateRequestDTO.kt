package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class UserUpdateRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    var userId: Long = 0,

    val username: String,

    val password: String,

    val nickname: String = "",

    val avatar: String = "",

    val phone: String = "",

    val email: String = "",

    val gender: String = "",

    val roles: String = "",

    val signature: String = "",
    val settings: String = "",
)
