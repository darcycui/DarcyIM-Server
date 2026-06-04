package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequestDTO(
    @field:NotBlank(message = "用户名不能为空")
    @field:Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    val username: String,

    @field:NotBlank(message = "密码不能为空")
    @field:Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    val password: String,

    @field:NotBlank(message = "昵称不能为空")
    val nickname: String = "",

    @field:NotBlank(message = "头像不能为空")
    val avatar: String = "",

    @field:NotBlank(message = "性别不能为空")
    val phone: String = "",

    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式不正确")
    val email: String="",

    @field:NotBlank(message = "性别不能为空")
    val gender: String = "",

    @field:NotBlank(message = "角色不能为空")
    val roles: String = "",

    val signature: String = "",
    val settings: String = "",
) : IRequestDTO
