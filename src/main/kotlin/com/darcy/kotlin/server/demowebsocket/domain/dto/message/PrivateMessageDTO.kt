package com.darcy.kotlin.server.demowebsocket.domain.dto.message

import com.darcy.kotlin.server.demowebsocket.domain.table.User
import com.darcy.kotlin.server.demowebsocket.domain.table.message.PrivateMessage
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class PrivateMessageDTO(
    val msgId: String = "",
    val senderId: Long = 0,
    val senderName: String = "",
    val receiverId: Long = 0,
    val receiverName: String = "",
    val content: String = "",
    val msgType: String = "TEXT",
    val sendTime: String = TimeUtil.formatDateTimeToString(LocalDateTime.now()),
    val isRecalled: Boolean = false,
    val dhPublicKey: String = "",
    val nKey: Long = 0L,
    val pnKey: Long = 0L
)

fun PrivateMessage.toDTO(): PrivateMessageDTO {
    return PrivateMessageDTO(
        msgId = this.msgId,
        senderId = this.sender.id,
        senderName = this.sender.username,
        receiverId = this.receiver.id,
        receiverName = this.receiver.username,
        content = this.content,
        msgType = this.msgType.name,
        sendTime = TimeUtil.formatDateTimeToString(this.sendTime),
        isRecalled = this.isRecalled,
        dhPublicKey = this.dhPublicKey,
        nKey = this.nKey,
        pnKey = this.pnKey
    )
}

fun List<PrivateMessage>.toDTO(): List<PrivateMessageDTO> {
    return this.map { it.toDTO() }
}

fun Page<PrivateMessage>.toDTO(): Page<PrivateMessageDTO> {
    return this.map { it.toDTO() }
}

fun PrivateMessageDTO.toEntity(
    sender: User,
    receiver: User,
    dhPublicKey: String,
    N: Long,
    PN: Long
): PrivateMessage {
    return PrivateMessage(
        msgId = this.msgId,
        sender = sender,
        receiver = receiver,
        content = this.content,
        msgType = PrivateMessage.MessageType.valueOf(this.msgType),
        sendTime = TimeUtil.parseStringToDateTime(this.sendTime),
        dhPublicKey = dhPublicKey,
        nKey = N,
        pnKey = PN
    )
}
