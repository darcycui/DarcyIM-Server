package com.darcy.kotlin.server.demowebsocket.domain.dto.user

import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.data.domain.Page

data class UserDTO(
    val username: String = "",
    val passwordHash: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val phone: String = "",
    val email: String = "",
    val gender: String = "",
    val signature: String = "",
    val status: Int = 0,
    val onlineStatus: Int = 0,
    val lastActiveTime: String = "",
    val deletedAt: String = "",
    val settings: Map<String, Any> = emptyMap(),
    val roles: String = "",
    val token: String = "",
    val id: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun User.toDTO(): UserDTO {
    return UserDTO(
        username = this.username,
        passwordHash = "",
        nickname = this.nickname,
        avatar = this.avatar,
        phone = this.phone,
        email = this.email,
        gender = this.gender,
        signature = this.signature,
        status = this.status.toCode(),
        onlineStatus = this.onlineStatus.toCode(),
        lastActiveTime = TimeUtil.formatDateTimeToString(this.lastActiveTime),
        deletedAt = TimeUtil.formatDateTimeToString(this.deletedAt),
        settings = this.settings,
        roles = this.roles,
        token = this.token,
        id = this.id,
        createdAt = TimeUtil.formatDateTimeToString(this.createdAt),
        updatedAt = TimeUtil.formatDateTimeToString(this.updatedAt)
    )
}

fun List<User>.toDTO(): List<UserDTO> {
    return this.map { it.toDTO() }
}

fun Page<User>.toDTO(): Page<UserDTO> {
    return this.map { it.toDTO() }
}

fun UserDTO.toEntity(): User {
    return User(
        username = this.username,
        passwordHash = this.passwordHash,
        nickname = this.nickname,
        avatar = this.avatar,
        phone = this.phone,
        email = this.email,
        gender = this.gender,
        signature = this.signature,
        status = User.UserStatus.fromCode(this.status),
        onlineStatus = User.OnlineStatus.fromCode(this.onlineStatus),
        lastActiveTime = TimeUtil.parseStringToDateTime(this.lastActiveTime),
        deletedAt = TimeUtil.parseStringToDateTime(this.deletedAt),
        settings = this.settings,
        roles = this.roles,
        token = this.token
    ).apply {
    }
}
