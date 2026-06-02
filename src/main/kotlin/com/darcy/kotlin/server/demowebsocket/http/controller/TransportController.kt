package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.ITransportApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.TransportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class TransportController @Autowired constructor(
    private val transportService: TransportService
) : ITransportApi {
    override fun dhKeyExchange(params: Map<String, String>): String {
        val userId = params["userId"]
            ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val publicKey = params["publicKey"]
            ?: throw ParamsException.ParamsNotValid(mapOf("publicKey" to "DH公钥不能为空"))
        val result = transportService.dhKeyExchange(userId, publicKey)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }
}