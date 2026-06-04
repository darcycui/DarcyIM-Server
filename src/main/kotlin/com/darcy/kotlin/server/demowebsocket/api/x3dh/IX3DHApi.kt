package com.darcy.kotlin.server.demowebsocket.api.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPullHelloRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPullKeysRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPushHelloRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.X3DHPushKeysRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.HelloMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.X3DHPullKeysDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.X3DHPushKeysDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/x3dh")
interface IX3DHApi {
    @PostMapping("/push/keys")
    fun pushKeys(@RequestBody params: X3DHPushKeysRequestDTO): X3DHPushKeysDTO

    @PostMapping("/pull/keys")
    fun pullKeys(@RequestBody params: X3DHPullKeysRequestDTO): X3DHPullKeysDTO

    @PostMapping("/push/alice/hello")
    fun pushAliceHelloMessage(@RequestBody params: X3DHPushHelloRequestDTO): HelloMessageDTO

    @PostMapping("/pull/alice/hello")
    fun pullAliceHello(@RequestBody params: X3DHPullHelloRequestDTO): HelloMessageDTO
}