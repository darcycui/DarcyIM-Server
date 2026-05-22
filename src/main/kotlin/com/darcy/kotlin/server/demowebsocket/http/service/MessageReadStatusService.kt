package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.dto.message.MessageReadStatusDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.message.MessageReadStatus
import com.darcy.kotlin.server.demowebsocket.http.repository.MessageReadStatusRepository
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MessageReadStatusService @Autowired constructor(
    private val messageReadStatusRepository: MessageReadStatusRepository,
    private val userService: UserService
) {

    @Transactional
    fun senderCreateOrUpdateReadStatus(
        msgId: String,
        userId: Long,
        conversationType: Conversation.ConversationType,
        targetId: Long,
        isRead: Boolean = false,
        clientType: String = "",
        deviceId: String = ""
    ): MessageReadStatus {
        val user = userService.queryUserById(userId)
        messageReadStatusRepository.senderFindBySenderIdAndMsgId(userId, msgId)?.apply {
            messageReadStatusRepository.delete(this)
        }
        val newStatus = MessageReadStatus(
            msgId = msgId,
            user = user,
            conversationType = conversationType,
            targetId = targetId,
            isRead = isRead,
            readTime = if (isRead) LocalDateTime.now() else null,
            clientType = clientType,
            deviceId = deviceId
        )
        return messageReadStatusRepository.save(newStatus)
    }

    @Transactional
    fun receiverMarkMessagesAsRead(userId: Long, msgIds: List<String>): Int {
        if (msgIds.isEmpty()) return 0
        val readTime = LocalDateTime.now()
        val updatedCount = messageReadStatusRepository.receiverMarkMessagesAsRead(userId, msgIds, readTime)
        DarcyLogger.info("批量标记消息已读: userId=$userId, count=$updatedCount")
        return updatedCount
    }

    fun receiverGetMessageListReadStatus(userId: Long, msgIds: List<String>): MessageReadStatusDTO {
        return messageReadStatusRepository.receiverFindByUserIdAndMsgIdList(userId, msgIds).toDTO()
    }

    fun receiverGetUnreadMessageListByConversation(userId: Long, targetId: Long): MessageReadStatusDTO {
        return messageReadStatusRepository.receiverFindUnreadMessageListByConversation(userId, targetId).toDTO()
    }

    fun deleteByUserIdAndTargetId(userId: Long, friendId: Long): Int {
        return messageReadStatusRepository.deleteByUserIdAndTargetId(userId, friendId)
    }

    fun senderSyncMessageReadStatus(userId: Long, targetId: Long): MessageReadStatusDTO {
        return messageReadStatusRepository.senderFindReadMessageListByConversation(userId, targetId).toDTO()
    }
}
