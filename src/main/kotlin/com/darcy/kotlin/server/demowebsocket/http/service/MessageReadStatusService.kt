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
    fun createOrUpdateReadStatus(
        msgId: String,
        userId: Long,
        conversationType: Conversation.ConversationType,
        targetId: Long,
        isRead: Boolean = false,
        clientType: String = "",
        deviceId: String = ""
    ): MessageReadStatus {
        val user = userService.queryUserById(userId)
        val existingStatus = messageReadStatusRepository.findByUserIdAndMsgId(userId, msgId)
        return if (existingStatus != null) {
            existingStatus.apply {
                this.conversationType = conversationType
                this.targetId = targetId
                this.isRead = isRead
                if (isRead && readTime == null) {
                    this.readTime = LocalDateTime.now()
                }
                this.clientType = clientType
                this.deviceId = deviceId
            }.let { messageReadStatusRepository.save(it) }
        } else {
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
            messageReadStatusRepository.save(newStatus)
        }
    }

    @Transactional
    fun markMessagesAsRead(userId: Long, msgIds: List<String>): Int {
        if (msgIds.isEmpty()) return 0
        val readTime = LocalDateTime.now()
        val updatedCount = messageReadStatusRepository.markMessagesAsRead(userId, msgIds, readTime)
        DarcyLogger.info("批量标记消息已读: userId=$userId, count=$updatedCount")
        return updatedCount
    }

    fun getUnreadMessagesByConversation(userId: Long, targetId: Long): MessageReadStatusDTO {
        return messageReadStatusRepository.findUnreadMessagesByConversation(userId, targetId).toDTO()
    }

    fun getMessageReadStatus(userId: Long, msgId: String): MessageReadStatusDTO {
        return messageReadStatusRepository.findByUserIdAndMsgId(userId, msgId).toDTO()
    }

    fun getMessagesReadStatus(userId: Long, msgIds: List<String>): MessageReadStatusDTO {
        return messageReadStatusRepository.findByUserIdAndMsgIds(userId, msgIds).toDTO()
    }

    fun deleteByUserIdAndTargetId(userId: Long, friendId: Long): Int {
        return messageReadStatusRepository.deleteByUserIdAndTargetId(userId, friendId)
    }
}
