package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.group.Group
import com.darcy.kotlin.server.demowebsocket.domain.table.group.GroupMember
import com.darcy.kotlin.server.demowebsocket.exception.code900.GroupException
import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.http.repository.GroupRepository
import com.darcy.kotlin.server.demowebsocket.utils.IdGenerator
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GroupService @Autowired constructor(
    private val groupRepository: GroupRepository,
    private val groupMemberService: GroupMemberService,
    private val groupInviteService: GroupInviteService,
    // 循环依赖 使用 @Lazy 解决
    @Lazy private val conversationService: ConversationService,
    private val userService: UserService,
    private val idGenerator: IdGenerator,
) {
    @Transactional
    fun createGroup(params: Map<String, String>): Group {
        val ownerId = params["ownerId"]?.toLongOrNull() ?: throw UserException.USER_NOT_EXIST
        val owner = userService.queryUserById(ownerId)
        val group = Group(
            // 群ID 唯一
            groupId = idGenerator.nextGroupId(),
            groupName = params["groupName"] as String,
            owner = owner,
            maxMembers = 20,
            currentMembers = 1,
            joinType = Group.JoinType.FREE,
            joinQuestion = "",
            joinAnswer = "",
        )
        val groupResult = groupRepository.save(group)
        // 把创建者 添加到群成员
        val member = GroupMember(
            user = owner,
            group = groupResult,
            role = GroupMember.MemberRole.OWNER,
            joinTime = LocalDateTime.now(),
        )
        groupMemberService.addGroupMember(member)
        // 创建群聊会话
        conversationService.createConversation(ownerId, Conversation.ConversationType.GROUP, groupResult.id)
        return groupResult
    }

    @Transactional
    fun updateGroup(params: Map<String, String>): Group {
        val id = params["groupId"]?.toLongOrNull() ?: throw GroupException.GROUP_NOT_EXIST
        val groupName = params["groupName"] as String
        val group = groupRepository.findById(id).orElse(null) ?: throw GroupException.GROUP_NOT_EXIST
        if (groupName == group.groupName) {
            throw GroupException.GROUP_INFO_NOT_CHANGED
        }
        val groupNew = group.apply { this.groupName = groupName }
        return groupRepository.save(groupNew)
    }

    @Transactional
    fun deleteGroup(id: Long): String {
        return kotlin.runCatching {
            val group = groupRepository.findById(id).orElse(null) ?: throw GroupException.GROUP_NOT_EXIST
            val deleteCount = groupRepository.deleteById(id)
            "成功删除群组:$id ${group.groupName}"
        }.onFailure {
            throw GroupException.GROUP_DELETE_FAILED
        }.getOrElse { it.message ?: "" }
    }

    @Transactional
    fun queryGroupById(id: Long): Group {
        val group = groupRepository.findById(id)
        if (group.isEmpty) throw GroupException.GROUP_NOT_EXIST
        return group.get()
    }

    @Transactional
    fun inviteToGroup(inviterId: Long, inviteeId: Long, groupId: Long): GroupMember {
        val group = queryGroupById(groupId)
        val inviter = userService.queryUserById(inviterId)
        val invitee = userService.queryUserById(inviteeId)
        // 邀请记录
        groupInviteService.createGroupInvite(inviterId, inviteeId, groupId)
        val member = GroupMember(
            user = invitee,
            group = group,
            role = GroupMember.MemberRole.OWNER,
            joinTime = LocalDateTime.now(),
        )
        // 添加被邀请人到群成员
        val result = groupMemberService.addGroupMember(member)
        // 创建群聊会话
        conversationService.createConversation(inviteeId, Conversation.ConversationType.GROUP, groupId)
        return result
    }

    fun queryAllGroupMembersById(groupId: Long): List<GroupMember> {
        return groupMemberService.queryGroupMembers(groupId)
    }
}