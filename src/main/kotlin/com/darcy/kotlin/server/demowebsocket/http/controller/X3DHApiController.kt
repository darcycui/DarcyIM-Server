package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.x3dh.IX3DHApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.toDTOList
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.X3DHService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class X3DHApiController @Autowired constructor(
    private val x3dhService: X3DHService,
) : IX3DHApi {
    override fun pushKeys(params: Map<String, String>): String {
        val userId =
            params["userId"]?.toLong() ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val result = x3dhService.createUserKeys(
            userId,
            params["identityKey"] ?: throw ParamsException.ParamsNotValid(mapOf("identityKey" to "用户公钥不能为空")),
            params["signedPreKey"]
                ?: throw ParamsException.ParamsNotValid(mapOf("signedPreKey" to "已签名的预密钥不能为空")),
            params["oneTimePreKeys"]
                ?: throw ParamsException.ParamsNotValid(mapOf("oneTimePreKeys" to "一次性预密钥不能为空"))
        )
        return ResultEntity.success(result).toJsonString()
    }

    override fun pullKeys(params: Map<String, String>): String {
        val aliceUserId = params["aliceUserId"]?.toLong()
            ?: throw ParamsException.ParamsNotValid(mapOf("aliceUserId" to "用户ID不能为空"))
        val bobUserId = params["bobUserId"]?.toLong()
            ?: throw ParamsException.ParamsNotValid(mapOf("bobUserId" to "用户ID不能为空"))
        val result = x3dhService.queryPublicKeys(aliceUserId, bobUserId)
        return ResultEntity.success(result).toJsonString()
    }

    override fun pushHelloMessage(params: Map<String, String>): String {
        val aliceUserId = params["aliceUserId"]?.toLong()
            ?: throw ParamsException.ParamsNotValid(mapOf("aliceUserId" to "用户ID不能为空"))
        val bobUserId = params["bobUserId"]?.toLong()
            ?: throw ParamsException.ParamsNotValid(mapOf("bobUserId" to "用户ID不能为空"))
        val aliceIdentityKey = params["aliceIdentityKey"]
            ?: throw ParamsException.ParamsNotValid(mapOf("aliceIdentityKey" to "用户身份公钥不能为空"))
        val aliceEphemeralKey = params["aliceEphemeralKey"]
            ?: throw ParamsException.ParamsNotValid(mapOf("aliceEphemeralKey" to "用户临时公钥不能为空"))
        val bobOneTimePreKeyIndex = params["bobOneTimePreKeyIndex"]?.toLong() ?: throw ParamsException.ParamsNotValid(
            mapOf("bobOneTimePreKeyIndex" to "用户一次性预密钥索引不能为空")
        )
        val result = x3dhService.createHelloMessage(
            aliceUserId,
            bobUserId,
            aliceIdentityKey,
            aliceEphemeralKey,
            bobOneTimePreKeyIndex
        )
        return ResultEntity.success(result).toJsonString()
    }

    override fun pullAliceHello(params: Map<String, String>): String {
        val aliceUserId = params["aliceUserId"]?.toLong()
            ?: throw ParamsException.ParamsNotValid(mapOf("aliceUserId" to "用户ID不能为空"))
        val bobUserId = params["bobUserId"]?.toLong() ?: throw ParamsException.ParamsNotValid(
            mapOf("bobUserId" to "用户ID不能为空")
        )
        val result = x3dhService.queryHelloMessage(aliceUserId, bobUserId).toDTOList()
        return ResultEntity.success(result).toJsonString()
    }
}