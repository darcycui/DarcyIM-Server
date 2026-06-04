package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IGroupInviteApi
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupInviteDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteQueryFromRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteQueryToRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.GroupInviteRequestDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.GroupInviteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupInviteController @Autowired constructor(
    private val groupInviteService: GroupInviteService
) : IGroupInviteApi {
    override fun createGroupInvite(params: GroupInviteRequestDTO): GroupInviteDTO {
        val result = groupInviteService.createGroupInvite(
            params.inviterId, params.inviteeId, params.groupId
        )
        return result.toDTO()
    }

    override fun queryGroupInviteByFromUser(params: GroupInviteQueryFromRequestDTO): List<GroupInviteDTO> {
        val result = groupInviteService.queryGroupInvitesByFromUser(params.fromUserId)
        return result.toDTO()
    }

    override fun queryGroupInviteByToUser(params: GroupInviteQueryToRequestDTO): List<GroupInviteDTO> {
        val result = groupInviteService.queryGroupInvitesByToUser(params.toUserId)
        return result.toDTO()
    }

}