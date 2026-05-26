package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverOfflineMessageSyncInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.*
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.message.MessageReadStatus
import com.darcy.kotlin.server.demowebsocket.domain.table.message.PrivateMessage
import com.darcy.kotlin.server.demowebsocket.http.repository.MessageReadStatusRepository
import com.darcy.kotlin.server.demowebsocket.http.repository.PrivateMessageRepository
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MessageReadStatusService @Autowired constructor(
    private val messageReadStatusRepository: MessageReadStatusRepository,
    private val userService: UserService,
    private val privateMessageRepository: PrivateMessageRepository
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

    fun deleteByUserIdAndTargetId(userId: Long, friendId: Long): Int {
        return messageReadStatusRepository.deleteByUserIdAndTargetId(userId, friendId)
    }

    // 新增方法：支持时间范围查询离线期间的已读状态
    fun senderSyncOfflineMessageReadStatus(
        userId: Long,
        targetId: Long,
        since: String?,
        until: String?
    ): MessageReadStatusDTO {
        // 如果客户端没有提供 since，则从用户表中获取 lastActiveTime
        val actualSince = since ?: run {
            val user = userService.queryUserById(userId)
            TimeUtil.formatDateTimeToString(user.lastActiveTime)
        }
        val sinceTime = TimeUtil.parseStringToDateTime(actualSince)
        val untilTime = until?.let { TimeUtil.parseStringToDateTime(it) } ?: LocalDateTime.now()

        return messageReadStatusRepository.senderFindReadMessageListByConversationWithTimeRange(
            userId, targetId, sinceTime, untilTime
        ).toDTO()
    }


    /**
     * 接收方离线消息同步（核心方法）
     * 参考 queryBothMessagesPageByConversation 重构为 Page<PrivateMessage>
     * 支持标准分页、增量同步
     */
    @Transactional(readOnly = true)
    fun receiverPullOfflineMessages(input: ReceiverOfflineMessageSyncInputDTO): Page<PrivateMessage> {
        val userId = input.userId
        val targetId = input.targetId
        val page = (input.page ?: 1) - 1  // 客户端页码从1开始 Page默认从0开始 这里需要转换索引
        val size = if (input.size in 1..100) input.size ?: 50 else 50
        val pageable = PageRequest.of(page, size)

        DarcyLogger.info("接收方离线同步: userId=$userId, targetId=$targetId, page=$page, size=$size")

        // 根据参数选择不同的查询策略
        // 策略1：基于时间戳的分页查询
        val sinceTime = if (input.lastSyncTime?.isNotEmpty() == true) {
            TimeUtil.parseStringToDateTime(input.lastSyncTime)
        } else {
            userService.queryLastActiveTime(userId)
        } ?: TimeUtil.defaultDateTime()
        val messagesPage =
            privateMessageRepository.findMessagesSinceTimePage(
                userId, targetId, sinceTime, pageable
            )

        DarcyLogger.info("离线同步完成: totalElements=${messagesPage.totalElements}, totalPages=${messagesPage.totalPages}, currentPage=${messagesPage.content.size}")

        return messagesPage
    }
}
