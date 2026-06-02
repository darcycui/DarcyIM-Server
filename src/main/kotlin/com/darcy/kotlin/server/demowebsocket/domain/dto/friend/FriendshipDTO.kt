package com.darcy.kotlin.server.demowebsocket.domain.dto.friend

import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.friend.Friendship
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.data.domain.Page

data class FriendshipDTO(
    val user: UserDTO = UserDTO(),
    val friend: UserDTO = UserDTO(),
    val alias: String = "",
    val relationStatus: Int = 0,
    val source: String = "",
    val greeting: String = "",
    val addedAt: String = "",
    val isBlocked: Boolean = false,
    val isMuted: Boolean = false,
    val isPinned: Boolean = false,
    val id: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun Friendship.toDTO(): FriendshipDTO {
    return FriendshipDTO(
        user = this.user.toDTO(),
        friend = this.friend.toDTO(),
        alias = this.alias,
        relationStatus = this.relationStatus.toCode(),
        source = this.source,
        greeting = this.greeting,
        addedAt = TimeUtil.formatDateTimeToString(this.addedAt),
        isBlocked = this.isBlocked,
        isMuted = this.isMuted,
        isPinned = this.isPinned,
        id = this.id,
        createdAt = TimeUtil.formatDateTimeToString(this.createdAt),
        updatedAt = TimeUtil.formatDateTimeToString(this.updatedAt)
    )
}

fun List<Friendship>.toDTO(): List<FriendshipDTO> {
    return this.map { it.toDTO() }
}

fun Page<Friendship>.toDTO(): Page<FriendshipDTO> {
    return this.map { it.toDTO() }
}

fun FriendshipDTO.toEntity(user: User, friend: User): Friendship {
    return Friendship(
        user = user,
        friend = friend,
        alias = this.alias,
        relationStatus = Friendship.RelationStatus.fromCode(this.relationStatus),
        source = this.source,
        greeting = this.greeting,
        addedAt = TimeUtil.parseStringToDateTime(this.addedAt),
        isBlocked = this.isBlocked,
        isMuted = this.isMuted,
        isPinned = this.isPinned
    ).apply {
        this.id = this@toEntity.id
    }
}
