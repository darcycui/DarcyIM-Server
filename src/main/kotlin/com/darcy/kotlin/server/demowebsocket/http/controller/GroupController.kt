package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IGroupApi
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupMemberDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupQueryRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupUpdateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.toDTO
import com.darcy.kotlin.server.demowebsocket.http.service.GroupMemberService
import com.darcy.kotlin.server.demowebsocket.http.service.GroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupController @Autowired constructor(
    private val groupService: GroupService,
    private val groupMemberService: GroupMemberService,
) : IGroupApi {
    override fun createGroup(params: GroupCreateRequestDTO): GroupDTO {
        val result = groupService.createGroup(params.ownerId, params.groupName)
        return result.toDTO()
    }

    override fun updateGroup(params: GroupUpdateRequestDTO): GroupDTO {
        val result = groupService.updateGroup(params.groupId, params.groupName)
        return result.toDTO()
    }

    override fun deleteGroup(params: GroupQueryRequestDTO): StringDTO {
        val result = groupService.deleteGroup(params.groupId)
        return result.toDTO()
    }

    override fun queryGroupById(params: GroupQueryRequestDTO): GroupDTO {
        val result = groupService.queryGroupById(params.groupId)
        return result.toDTO()
    }

    override fun inviteToGroup(params: GroupInviteCreateRequestDTO): GroupMemberDTO {
        val result = groupService.inviteToGroup(
            params.inviterId, params.inviteeId, params.groupId
        )
        return result.toDTO()
    }

    override fun queryGroupMembers(params: GroupQueryRequestDTO): List<GroupMemberDTO> {
        val result = groupMemberService.queryGroupMembers(params.groupId)
        return result.toDTO()
    }
}