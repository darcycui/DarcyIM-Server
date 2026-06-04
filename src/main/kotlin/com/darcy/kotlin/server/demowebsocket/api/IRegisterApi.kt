package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.RegisterRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api")
interface IRegisterApi {
    @PostMapping("/register")
    fun register(@RequestBody @Valid params: RegisterRequestDTO): UserDTO
}