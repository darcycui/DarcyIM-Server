package com.darcy.kotlin.server.demowebsocket.domain.dto.conversation

import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.data.domain.Page

data class ConversationDTO(
    val conversationId: String = "",
    val user: UserDTO,
    val conversationType: Int,
    val target: UserDTO = UserDTO(),
    val lastMsgId: String = "",
    val lastMsgContent: String = "",
    val lastMsgType: Int = 0,
    val lastMsgSenderId: Long = 0L,
    val lastMsgTime: String = "",
    val unreadCount: Int = 0,
    val isMuted: Boolean = false,
    val isPinned: Boolean = false,
    val isTop: Boolean = false,
    val draft: String = "",
    val draftTime: String = "",
    val extData: Map<String, Any> = emptyMap(),
    val id: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun Conversation.toDTO(target: UserDTO): ConversationDTO {
    return ConversationDTO(
        conversationId = this.conversationId,
        user = this.user.toDTO(),
        conversationType = this.conversationType.code,
        target = target,
        lastMsgId = this.lastMsgId,
        lastMsgContent = this.lastMsgContent,
        lastMsgType = this.lastMsgType,
        lastMsgSenderId = this.lastMsgSenderId,
        lastMsgTime = TimeUtil.formatDateTimeToString(this.lastMsgTime),
        unreadCount = this.unreadCount,
        isMuted = this.isMuted,
        isPinned = this.isPinned,
        isTop = this.isTop,
        draft = this.draft,
        draftTime = TimeUtil.formatDateTimeToString(this.draftTime),
        extData = this.extData,
        id = this.id,
        createdAt = TimeUtil.formatDateTimeToString(this.createdAt),
        updatedAt = TimeUtil.formatDateTimeToString(this.updatedAt)
    )
}

fun List<Conversation>.toDTO(targetList: List<User>): List<ConversationDTO> {
    return this.mapIndexed { index, item ->
        item.toDTO(targetList[index].toDTO())
    }
}

fun Page<Conversation>.toDTO(targetList: List<User>): Page<ConversationDTO> {
    var index = -1
    return this.map { item ->
        index += 1
        item.toDTO(targetList[index].toDTO())
    }
}

fun ConversationDTO.toEntity(user: User): Conversation {
    return Conversation(
        conversationId = this.conversationId,
        user = user,
        conversationType = Conversation.ConversationType.fromCode(this.conversationType),
        targetId = this.target.id,
        lastMsgId = this.lastMsgId,
        lastMsgContent = this.lastMsgContent,
        lastMsgType = this.lastMsgType,
        lastMsgSenderId = this.lastMsgSenderId,
        lastMsgTime = TimeUtil.parseStringToDateTime(this.lastMsgTime),
        unreadCount = this.unreadCount,
        isMuted = this.isMuted,
        isPinned = this.isPinned,
        isTop = this.isTop,
        draft = this.draft,
        draftTime = TimeUtil.parseStringToDateTime(this.draftTime),
        extData = this.extData,
    ).apply {
    }
}
