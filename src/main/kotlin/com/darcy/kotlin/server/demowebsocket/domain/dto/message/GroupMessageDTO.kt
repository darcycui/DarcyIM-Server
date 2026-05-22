package com.darcy.kotlin.server.demowebsocket.domain.dto.message

import com.darcy.kotlin.server.demowebsocket.domain.table.User
import com.darcy.kotlin.server.demowebsocket.domain.table.group.Group
import com.darcy.kotlin.server.demowebsocket.domain.table.message.GroupMessage
import com.darcy.kotlin.server.demowebsocket.utils.UUIdGenerator
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class GroupMessageDTO(
    val msgId: String = "",
    val groupId: Long = 0L,
    val groupName: String = "",
    val senderId: Long = 0L,
    val senderName: String = "",
    val content: String = "",
    val msgType: String = "TEXT",
    val extraData: Map<String, Any> = emptyMap(),
    val isRecalled: Boolean = false,
    val seqId: Long = 0L,
    val sendTime: String = TimeUtil.formatDateTimeToString(LocalDateTime.now()),
    val recallTime: LocalDateTime? = null,
    val isAtAll: Boolean = false,
    val atUsers: List<Long> = emptyList(),
    val replyToMsgId: String = "",
    val readCount: Int = 0,
    val totalMembers: Int = 1
)

fun GroupMessage.toDTO(): GroupMessageDTO {
    return GroupMessageDTO(
        msgId = this.msgId,
        groupId = this.group.id,
        groupName = this.group.groupName,
        senderId = this.sender.id,
        senderName = this.sender.username,
        content = this.content,
        msgType = this.msgType.name,
        extraData = this.extraData,
        isRecalled = this.isRecalled,
        seqId = this.seqId,
        sendTime = TimeUtil.formatDateTimeToString(this.sendTime),
        recallTime = this.recallTime,
        isAtAll = this.isAtAll,
        atUsers = this.atUsers,
        replyToMsgId = this.replyToMsgId,
        readCount = this.readCount,
        totalMembers = this.totalMembers
    )
}

fun List<GroupMessage>.toDTO(): List<GroupMessageDTO> {
    return this.map { it.toDTO() }
}

fun Page<GroupMessage>.toDTO(): Page<GroupMessageDTO> {
    return this.map { it.toDTO() }
}

fun GroupMessageDTO.toEntity(
    sender: User,
    group: Group,
): GroupMessage {
    return GroupMessage(
        msgId = UUIdGenerator().nextGroupId(),
        group = group,
        sender = sender,
        content = this.content,
        msgType = GroupMessage.MessageType.valueOf(this.msgType),
        extraData = this.extraData,
        isRecalled = this.isRecalled,
        seqId = this.seqId,
        sendTime = TimeUtil.parseStringToDateTime(this.sendTime),
        recallTime = this.recallTime,
        isAtAll = this.isAtAll,
        atUsers = this.atUsers,
        replyToMsgId = this.replyToMsgId,
        readCount = this.readCount,
        totalMembers = this.totalMembers
    )
}
