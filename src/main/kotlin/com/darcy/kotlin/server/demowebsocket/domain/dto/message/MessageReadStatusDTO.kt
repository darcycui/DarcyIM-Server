package com.darcy.kotlin.server.demowebsocket.domain.dto.message

import com.darcy.kotlin.server.demowebsocket.domain.table.message.MessageReadStatus
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil

data class MessageReadStatusDTO(
    val id: Long = 0,
    val msgIds: List<String> = listOf(),
    val userId: Long = 0,
    val conversationType: Int = 1,
    val targetId: Long = 0L,
    val isRead: Boolean = false,
    val readTime: String = "",
    val clientType: String = "",
    val deviceId: String = "",
)

fun MessageReadStatus?.toDTO(): MessageReadStatusDTO {
    if (this == null) return MessageReadStatusDTO()
    return MessageReadStatusDTO(
        id = this.id,
        msgIds = listOf(this.msgId),
        userId = this.user.id,
        conversationType = this.conversationType.code,
        targetId = this.targetId,
        isRead = this.isRead,
        readTime = TimeUtil.formatDateTimeToString(this.readTime),
        clientType = this.clientType,
        deviceId = this.deviceId
    )
}

fun List<MessageReadStatus>.toDTO(): MessageReadStatusDTO {
    if (this.isEmpty()) return MessageReadStatusDTO()
    val first = this.first()
    return MessageReadStatusDTO(
        id = first.id,
        msgIds = this.map { it.msgId },
        userId = first.user.id,
        conversationType = first.conversationType.code,
        targetId = first.targetId,
        isRead = first.isRead,
        readTime = TimeUtil.formatDateTimeToString(first.readTime),
        clientType = first.clientType,
        deviceId = first.deviceId
    )
}
