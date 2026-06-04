package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupMemberDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupUpdateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/groups")
interface IGroupApi {
    @PostMapping("/create")
    fun createGroup(@RequestBody @Valid params: GroupCreateRequestDTO): GroupDTO

    @PostMapping("/update")
    fun updateGroup(@RequestBody @Valid params: GroupUpdateRequestDTO): GroupDTO

    @PostMapping("/delete")
    fun deleteGroup(@RequestBody @Valid params: GroupQueryRequestDTO): StringDTO

    @PostMapping("/query/id")
    fun queryGroupById(@RequestBody @Valid params: GroupQueryRequestDTO): GroupDTO

    @PostMapping("/invite")
    fun inviteToGroup(@RequestBody @Valid params: GroupInviteCreateRequestDTO): GroupMemberDTO

    @PostMapping("/query/members")
    fun queryGroupMembers(@RequestBody @Valid params: GroupQueryRequestDTO): List<GroupMemberDTO>


}