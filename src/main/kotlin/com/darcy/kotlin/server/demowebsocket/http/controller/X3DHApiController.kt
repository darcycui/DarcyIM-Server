package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.x3dh.IX3DHApi
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPullHelloRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPullKeysRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPushHelloRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPushKeysRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.HelloMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.X3DHPullKeysDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.X3DHPushKeysDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.X3DHService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
@Encrypted
class X3DHApiController @Autowired constructor(
    private val x3dhService: X3DHService,
) : IX3DHApi {
    override fun pushKeys(params: X3DHPushKeysRequestDTO): X3DHPushKeysDTO {
        val userId =
            params.userId
        val result = x3dhService.createUserKeys(
            userId,
            params.identityKey,
            params.signedPreKey,
            params.oneTimePreKeys
        )
        return result
    }

    override fun pullKeys(params: X3DHPullKeysRequestDTO): X3DHPullKeysDTO {
        val result = x3dhService.queryUserKeys(params.aliceUserId, params.bobUserId)
        return result
    }

    override fun pushAliceHelloMessage(params: X3DHPushHelloRequestDTO): HelloMessageDTO {
        val result = x3dhService.createHelloMessage(
            params.aliceUserId,
            params.bobUserId,
            params.aliceIdentityKey,
            params.aliceEphemeralKey,
            params.bobOneTimePreKeyId
        )
        return result.toDTO()
    }

    override fun pullAliceHello(params: X3DHPullHelloRequestDTO): HelloMessageDTO {
        val result = x3dhService.queryHelloMessage(
            params.aliceUserId, params.bobUserId
        )
        return result.toDTO()
    }
}