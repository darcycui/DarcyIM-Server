package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IUserApi
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserQueryEmailRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserQueryIdRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserQueryPhoneRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserUpdateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
@Encrypted
class UserController @Autowired constructor(val userService: UserService) : IUserApi {

    override fun updateUser(params: UserUpdateRequestDTO): UserDTO {
        val result = userService.updateUser(params.userId,  params)
        return result.toDTO()
    }

    override fun deleteUser(params: UserQueryIdRequestDTO): StringDTO {
        val result = userService.deleteUser(params.userId)
        return result.toDTO()
    }

    override fun getUserById(params: UserQueryIdRequestDTO): UserDTO {
        val result = userService.queryUserById(params.userId)
        return result.toDTO()
    }

    override fun getUserByPhone(params: UserQueryPhoneRequestDTO): UserDTO {
        val result = userService.queryUserByPhone(params.phone)
        return result.toDTO()
    }

    override fun getUserByEmail(params: UserQueryEmailRequestDTO): UserDTO {
        val result = userService.queryUserByEmail(params.email)
        return result.toDTO()
    }
}