package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UserUpdateRequestDTO(
    @field:NotNull(message = "用户ID不能为空")
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

data class UserQueryIdRequestDTO(
    @field:NotNull(message = "用户ID不能为空")
    var userId: Long = 0
) : IRequestDTO

data class UserQueryPhoneRequestDTO(
    @field:NotBlank(message = "手机号不能为空")
    var phone: String = ""
) : IRequestDTO

data class UserQueryEmailRequestDTO(
    @field:NotBlank(message = "邮箱不能为空")
    var email: String = ""
) : IRequestDTO

data class UserQueryNameRequestDTO(
    @field:NotBlank(message = "用户名不能为空")
    var username: String = ""
) : IRequestDTO