package com.darcy.kotlin.server.demowebsocket.domain.dto.group

import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.domain.table.group.Group
import com.darcy.kotlin.server.demowebsocket.domain.table.group.GroupInvite
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil

data class GroupInviteDTO(
    val groupId: String = "",
    val inviterId: Long,
    val inviteeId: Long,
    val message: String = "",
    val status: Int = 0,
    val expireTime: String = "",
    val handleTime: String = "",
    val handleRemark: String = "",
    val isReminded: Boolean = false,
    val remindCount: Int = 0,
    val id: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun GroupInvite.toDTO(): GroupInviteDTO {
    return GroupInviteDTO(
        groupId = this.group.groupId,
        inviterId = this.inviter.id,
        inviteeId = this.invitee.id,
        message = this.message,
        status = this.status.toCode(),
        expireTime = TimeUtil.formatDateTimeToString(this.expireTime),
        handleTime = TimeUtil.formatDateTimeToString(this.handleTime),
        handleRemark = this.handleRemark,
        isReminded = this.isReminded,
        remindCount = this.remindCount,
        id = this.id,
        createdAt = TimeUtil.formatDateTimeToString(this.createdAt),
        updatedAt = TimeUtil.formatDateTimeToString(this.updatedAt)
    )
}

fun List<GroupInvite>.toDTO(): List<GroupInviteDTO> {
    return this.map { it.toDTO() }
}

fun GroupInviteDTO.toEntity(group: Group, inviter: User, invitee: User): GroupInvite {
    return GroupInvite(
        group = group,
        inviter = inviter,
        invitee = invitee,
        message = this.message,
        status = GroupInvite.InviteStatus.fromCode(this.status),
        expireTime = TimeUtil.parseStringToDateTime(this.expireTime),
        handleTime = TimeUtil.parseStringToDateTime(this.handleTime),
        handleRemark = this.handleRemark,
        isReminded = this.isReminded,
        remindCount = this.remindCount
    ).apply {
    }
}
