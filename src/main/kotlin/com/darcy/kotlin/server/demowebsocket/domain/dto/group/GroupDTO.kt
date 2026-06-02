package com.darcy.kotlin.server.demowebsocket.domain.dto.group

import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.domain.table.group.Group
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil

data class GroupDTO(
    val groupId: String = "",
    val groupName: String = "",
    val avatar: String = "",
    val description: String = "",
    val ownerId: Long,
    val maxMembers: Int = 500,
    val currentMembers: Int = 1,
    val joinType: Int = 2,
    val joinQuestion: String = "",
    val joinAnswer: String = "",
    val announcement: String = "",
    val announcementPublisherId: Long = 0L,
    val announcementPublishTime: String = "",
    val status: Int = 1,
    val permissions: Map<String, Boolean> = emptyMap(),
    val id: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun Group.toDTO(): GroupDTO {
    return GroupDTO(
        groupId = this.groupId,
        groupName = this.groupName,
        avatar = this.avatar,
        description = this.description,
        ownerId = this.owner.id,
        maxMembers = this.maxMembers,
        currentMembers = this.currentMembers,
        joinType = this.joinType.code,
        joinQuestion = this.joinQuestion,
        joinAnswer = this.joinAnswer,
        announcement = this.announcement,
        announcementPublisherId = this.announcementPublisherId,
        announcementPublishTime = TimeUtil.formatDateTimeToString(this.announcementPublishTime),
        status = this.status.toCode(),
        permissions = this.permissions,
        id = this.id,
        createdAt = TimeUtil.formatDateTimeToString(this.createdAt),
        updatedAt = TimeUtil.formatDateTimeToString(this.updatedAt)
    )
}

fun List<Group>.toDTO(): List<GroupDTO> {
    return this.map { it.toDTO() }
}

fun GroupDTO.toEntity(owner: User): Group {
    return Group(
        groupId = this.groupId,
        groupName = this.groupName,
        avatar = this.avatar,
        description = this.description,
        owner = owner,
        maxMembers = this.maxMembers,
        currentMembers = this.currentMembers,
        joinType = Group.JoinType.fromCode(this.joinType),
        joinQuestion = this.joinQuestion,
        joinAnswer = this.joinAnswer,
        announcement = this.announcement,
        announcementPublisherId = this.announcementPublisherId,
        announcementPublishTime = TimeUtil.parseStringToDateTime(this.announcementPublishTime),
        status = Group.GroupStatus.fromCode(this.status),
        permissions = this.permissions
    ).apply {
    }
}
