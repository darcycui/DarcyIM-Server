package com.darcy.kotlin.server.demowebsocket.domain.dto.friend

import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.friend.FriendRequest
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.data.domain.Page

data class FriendRequestDTO(
    val fromUser: UserDTO,
    val toUser: UserDTO,
    val greeting: String = "",
    val remark: String = "",
    val status: Int = 0,
    val handleTime: String = "",
    val handleResult: String = "",
    val id: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun List<FriendRequest>.toDTO(): List<FriendRequestDTO> {
    return this.map { it.toDTO() }
}

fun Page<FriendRequest>.toDTO(): Page<FriendRequestDTO> {
    return this.map { it.toDTO() }
}

fun FriendRequest.toDTO(): FriendRequestDTO {
    return FriendRequestDTO(
        fromUser = this.fromUser.toDTO(),
        toUser = this.toUser.toDTO(),
        greeting = this.greeting,
        remark = this.remark,
        status = this.status.toCode(),
        handleTime = TimeUtil.formatDateTimeToString(this.handleTime),
        handleResult = this.handleResult,
        id = this.id,
        createdAt = TimeUtil.formatDateTimeToString(this.createdAt),
        updatedAt = TimeUtil.formatDateTimeToString(this.updatedAt)
    )
}

fun FriendRequestDTO.toEntity(fromUser: User, toUser: User): FriendRequest {
    return FriendRequest(
        fromUser = fromUser,
        toUser = toUser,
        greeting = this.greeting,
        remark = this.remark,
        status = FriendRequest.RequestStatus.fromCode(this.status),
        handleTime = TimeUtil.parseStringToDateTime(this.handleTime),
        handleResult = this.handleResult
    ).apply {
    }
}
