package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IDHApi
import com.darcy.kotlin.server.demowebsocket.domain.dto.DHKeyExchangeDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.DHExchangeRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.toDTO
import com.darcy.kotlin.server.demowebsocket.http.service.DHService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class DHController @Autowired constructor(
    private val dhService: DHService
) : IDHApi {
    override fun dhKeyExchange(params: DHExchangeRequestDTO): DHKeyExchangeDTO {
        val result = dhService.dhKeyExchange(params.userId, params.publicKey)
        return result.toDTO()
    }
}