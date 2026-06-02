package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.dto.group.GroupMemberDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.group.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.group.GroupMember
import com.darcy.kotlin.server.demowebsocket.http.repository.GroupMemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupMemberService @Autowired constructor(
    private val groupMemberRepository: GroupMemberRepository
) {
    fun addGroupMember(groupMember: GroupMember): GroupMember {
        return groupMemberRepository.save(groupMember)
    }

    fun queryGroupMembers(id: Long): List<GroupMember> {
        return groupMemberRepository.findByGroupId(id)
    }

    fun isGroupMember(userId: Long, groupId: Long): Boolean {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId) != null
    }
}