package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.PrivateMessageQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.MessageSendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/private-messages")
interface IPrivateMessageApi {
    @PostMapping("/send")
    fun sendMessage(@RequestParam params: MessageSendRequestDTO): PrivateMessageDTO

    @PostMapping("/query/page")
    fun queryMessagesByConversation(@RequestParam params: PrivateMessageQueryRequestDTO): Page<PrivateMessageDTO>
}