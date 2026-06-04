package com.darcy.kotlin.server.demowebsocket

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupMessageQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupMessageSendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.GroupMessageDTO
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/group-messages")
interface IGroupMessageApi {
    @PostMapping("/send")
    fun sendMessage(@RequestParam params: GroupMessageSendRequestDTO): GroupMessageDTO

    @PostMapping("/query/page")
    fun queryGroupMessages(@RequestParam params: GroupMessageQueryRequestDTO): Page<GroupMessageDTO>
}