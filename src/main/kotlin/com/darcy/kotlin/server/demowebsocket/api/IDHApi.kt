package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.DHKeyExchangeDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.DHExchangeRequestDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/transport/dh")
interface IDHApi {
    @PostMapping("/exchange")
    fun dhKeyExchange(@RequestBody @Valid params: DHExchangeRequestDTO): DHKeyExchangeDTO
}