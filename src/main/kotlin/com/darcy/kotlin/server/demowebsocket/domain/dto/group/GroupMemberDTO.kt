package com.darcy.kotlin.server.demowebsocket.domain.dto.group

import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.domain.table.group.Group
import com.darcy.kotlin.server.demowebsocket.domain.table.group.GroupMember
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil

data class GroupMemberDTO(
    val groupId: String = "",
    val userId: Long,
    val alias: String = "",
    val role: Int = 0,
    val joinTime: String = "",
    val lastReadMsgId: Long = 0,
    val unreadCount: Int = 0,
    val isMuted: Boolean = false,
    val muteUntil: String = "",
    val isPinned: Boolean = false,
    val isShowNickname: Boolean = true,
    val inviterId: Long = 0L,
    val inviteTime: String = "",
    val id: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun GroupMember.toDTO(): GroupMemberDTO {
    return GroupMemberDTO(
        groupId = this.group.groupId,
        userId = this.user.id,
        alias = this.alias,
        role = this.role.toCode(),
        joinTime = TimeUtil.formatDateTimeToString(this.joinTime),
        lastReadMsgId = this.lastReadMsgId,
        unreadCount = this.unreadCount,
        isMuted = this.isMuted,
        muteUntil = TimeUtil.formatDateTimeToString(this.muteUntil),
        isPinned = this.isPinned,
        isShowNickname = this.isShowNickname,
        inviterId = this.inviterId,
        inviteTime = TimeUtil.formatDateTimeToString(this.inviteTime),
        id = this.id,
        createdAt = TimeUtil.formatDateTimeToString(this.createdAt),
        updatedAt = TimeUtil.formatDateTimeToString(this.updatedAt)
    )
}

fun List<GroupMember>.toDTO(): List<GroupMemberDTO> {
    return this.map { it.toDTO() }
}

fun GroupMemberDTO.toEntity(group: Group, user: User): GroupMember {
    return GroupMember(
        group = group,
        user = user,
        alias = this.alias,
        role = GroupMember.MemberRole.fromCode(this.role),
        joinTime = TimeUtil.parseStringToDateTime(this.joinTime),
        lastReadMsgId = this.lastReadMsgId,
        unreadCount = this.unreadCount,
        isMuted = this.isMuted,
        muteUntil = TimeUtil.parseStringToDateTime(this.muteUntil),
        isPinned = this.isPinned,
        isShowNickname = this.isShowNickname,
        inviterId = this.inviterId,
        inviteTime = TimeUtil.parseStringToDateTime(this.inviteTime)
    ).apply {
    }
}
