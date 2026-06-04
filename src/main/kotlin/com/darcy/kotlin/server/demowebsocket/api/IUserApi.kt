package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserQueryEmailRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserQueryIdRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserQueryPhoneRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.UserUpdateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/users")
interface IUserApi {

    @PostMapping("/update")
    fun updateUser(@RequestBody @Valid params: UserUpdateRequestDTO): UserDTO

    @PostMapping("/delete")
    fun deleteUser(@RequestBody @Valid params: UserQueryIdRequestDTO): StringDTO

    @PostMapping("/query/id")
    fun getUserById(@RequestBody @Valid params: UserQueryIdRequestDTO): UserDTO

    @PostMapping("/query/phone")
    fun getUserByPhone(@RequestBody @Valid params: UserQueryPhoneRequestDTO): UserDTO

    @PostMapping("/query/email")
    fun getUserByEmail(@RequestBody @Valid params: UserQueryEmailRequestDTO): UserDTO
}