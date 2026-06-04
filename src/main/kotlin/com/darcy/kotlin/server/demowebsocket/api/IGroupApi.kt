package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupMemberDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupUpdateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/groups")
interface IGroupApi {
    @PostMapping("/create")
    fun createGroup(@RequestBody params: GroupCreateRequestDTO): GroupDTO

    @PostMapping("/update")
    fun updateGroup(@RequestBody params: GroupUpdateRequestDTO): GroupDTO

    @PostMapping("/delete")
    fun deleteGroup(@RequestBody params: GroupQueryRequestDTO): StringDTO

    @PostMapping("/query/id")
    fun queryGroupById(@RequestBody params: GroupQueryRequestDTO): GroupDTO

    @PostMapping("/invite")
    fun inviteToGroup(@RequestBody params: GroupInviteRequestDTO): GroupMemberDTO

    @PostMapping("/query/members")
    fun queryGroupMembers(@RequestBody params: GroupQueryRequestDTO): List<GroupMemberDTO>


}