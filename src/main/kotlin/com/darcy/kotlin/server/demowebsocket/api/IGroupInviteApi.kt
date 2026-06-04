package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupInviteDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteQueryFromRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteQueryToRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteCreateRequestDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/group-invites")
interface IGroupInviteApi {
    @PostMapping("/create")
    fun createGroupInvite(@RequestBody @Valid params: GroupInviteCreateRequestDTO): GroupInviteDTO

    @PostMapping("/query/from")
    fun queryGroupInviteByFromUser(@RequestBody @Valid params: GroupInviteQueryFromRequestDTO): List<GroupInviteDTO>

    @PostMapping("/query/to")
    fun queryGroupInviteByToUser(@RequestBody @Valid params: GroupInviteQueryToRequestDTO): List<GroupInviteDTO>

}