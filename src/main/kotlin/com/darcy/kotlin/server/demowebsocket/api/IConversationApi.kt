package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.conversation.ConversationDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.CommonRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ConversationCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ConversationQueryRequestDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/conversations")
interface IConversationApi {
    @PostMapping("/create")
    fun createConversation(@RequestParam params: ConversationCreateRequestDTO): ConversationDTO

    @PostMapping("/query/all")
    fun queryConversations(@RequestParam params: CommonRequestDTO): List<ConversationDTO>

    @PostMapping("/query/id")
    fun queryConversationById(@RequestParam params: ConversationQueryRequestDTO): ConversationDTO
}