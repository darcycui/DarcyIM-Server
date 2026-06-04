package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.ILoginApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.LoginRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.LoginService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController @Autowired constructor(
    val loginService: LoginService
) : ILoginApi {

    override fun loginV2(@RequestBody @Valid params: LoginRequestDTO): UserDTO {
        val existUser = loginService.getValidateUser(params.phone, params.password)
        return existUser.toDTO()
    }
}