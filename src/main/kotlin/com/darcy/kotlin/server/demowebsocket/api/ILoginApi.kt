package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.LoginRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api")
interface ILoginApi {

    @PostMapping("/login")
    fun loginV2(@RequestBody @Valid params: LoginRequestDTO): UserDTO
}