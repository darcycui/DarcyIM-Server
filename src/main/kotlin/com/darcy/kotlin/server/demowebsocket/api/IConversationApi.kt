package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.conversation.ConversationDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.CommonRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ConversationCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ConversationQueryRequestDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/conversations")
interface IConversationApi {
    @PostMapping("/create")
    fun createConversation(@RequestBody @Valid params: ConversationCreateRequestDTO): ConversationDTO

    @PostMapping("/query/all")
    fun queryConversations(@RequestBody @Valid params: CommonRequestDTO): List<ConversationDTO>

    @PostMapping("/query/id")
    fun queryConversationById(@RequestBody @Valid params: ConversationQueryRequestDTO): ConversationDTO
}