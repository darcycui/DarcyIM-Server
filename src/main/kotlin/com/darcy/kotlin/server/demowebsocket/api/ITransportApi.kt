package com.darcy.kotlin.server.demowebsocket.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/transport/dh")
interface ITransportApi {
    @PostMapping("/exchange")
    fun dhKeyExchange(@RequestParam params: Map<String, String>): String
}