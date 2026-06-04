package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupInviteDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteQueryFromRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteQueryToRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteRequestDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/group-invites")
interface IGroupInviteApi {
    @PostMapping("/create")
    fun createGroupInvite(@RequestParam params: GroupInviteRequestDTO): GroupInviteDTO

    @PostMapping("/query/from")
    fun queryGroupInviteByFromUser(@RequestParam params: GroupInviteQueryFromRequestDTO): List<GroupInviteDTO>

    @PostMapping("/query/to")
    fun queryGroupInviteByToUser(@RequestParam params: GroupInviteQueryToRequestDTO): List<GroupInviteDTO>

}