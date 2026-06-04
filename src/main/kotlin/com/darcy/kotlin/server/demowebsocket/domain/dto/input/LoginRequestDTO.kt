package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class LoginRequestDTO(
    @field:NotBlank(message = "手机号不能为空")
    val phone: String = "",

    @field:NotBlank(message = "密码不能为空")
    val password: String = ""
) : IRequestDTO
