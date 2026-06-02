package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IGroupApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.GroupMemberService
import com.darcy.kotlin.server.demowebsocket.http.service.GroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupController @Autowired constructor(
    private val groupService: GroupService,
    private val groupMemberService: GroupMemberService,
) : IGroupApi {
    override fun createGroup(params: Map<String, String>): String {
        params["ownerId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("ownerId" to "创建者ID不能为空"))
        params["groupName"] ?: throw ParamsException.ParamsNotValid(mapOf("groupName" to "群组ID不能为空"))
        val result = groupService.createGroup(params)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun updateGroup(params: Map<String, String>): String {
        params["groupId"] ?: throw ParamsException.ParamsNotValid(mapOf("groupId" to "群组ID不能为空"))
        params["groupName"] ?: throw ParamsException.ParamsNotValid(mapOf("groupName" to "新群组名称不能为空"))
        val result = groupService.updateGroup(params)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun deleteGroup(params: Map<String, String>): String {
        val groupId = params["groupId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(mapOf("groupId" to "群组ID不能为空"))
        val result = groupService.deleteGroup(groupId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun queryGroupById(params: Map<String, String>): String {
        val groupId = params["groupId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(mapOf("groupId" to "群组ID不能为空"))
        val result = groupService.queryGroupById(groupId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun inviteToGroup(params: Map<String, String>): String {
        val inviterId = params["inviterId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("inviterId" to "邀请人ID不能为空"))
        val inviteeId = params["inviteeId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("inviteeId" to "被邀请人ID不能为空"))
        val groupId = params["groupId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(mapOf("groupId" to "群组ID不能为空"))
        val result = groupService.inviteToGroup(inviterId, inviteeId, groupId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun queryGroupMembers(params: Map<String, String>): String {
        val id = params["groupId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("groupId" to "群组ID不能为空"))
        val result = groupMemberService.queryGroupMembers(id)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }
}