package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.ILoginApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.LoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController @Autowired constructor(
    val loginService: LoginService
) : ILoginApi {
    override fun login(params: Map<String, String>): String {
        // username: String, password: String
        val phone = params["phone"] ?: throw ParamsException.ParamsNotValid(mapOf("phone" to "手机号不能为空"))
        val password = params["password"] ?: throw ParamsException.ParamsNotValid(mapOf("password" to "密码不能为空"))
        val existUser = loginService.getValidateUser(phone, password)
        return ResultEntity.success(existUser.toDTO()).toJsonString()
    }
}