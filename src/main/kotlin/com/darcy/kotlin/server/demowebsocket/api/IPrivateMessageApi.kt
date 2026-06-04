package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.PrivateMessageQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.PrivateMessageSendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/private-messages")
interface IPrivateMessageApi {
    @PostMapping("/send")
    fun sendMessage(@RequestBody @Valid params: PrivateMessageSendRequestDTO): PrivateMessageDTO

    @PostMapping("/query/page")
    fun queryMessagesByConversation(@RequestBody @Valid params: PrivateMessageQueryRequestDTO): Page<PrivateMessageDTO>
}