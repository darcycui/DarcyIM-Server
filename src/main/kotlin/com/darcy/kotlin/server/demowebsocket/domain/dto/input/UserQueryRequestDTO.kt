package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class UserQueryIdRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    var userId: Long = 0
)

data class UserQueryPhoneRequestDTO(
    @field:NotBlank(message = "手机号不能为空")
    var phone: String = ""
)

data class UserQueryEmailRequestDTO(
    @field:NotBlank(message = "邮箱不能为空")
    var email: String = ""
)

data class UserQueryNameRequestDTO(
    @field:NotBlank(message = "用户名不能为空")
    var username: String = ""
)

