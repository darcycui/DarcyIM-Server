package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverOfflineMessageSyncInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.*
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.message.MessageReadStatus
import com.darcy.kotlin.server.demowebsocket.http.repository.MessageReadStatusRepository
import com.darcy.kotlin.server.demowebsocket.http.repository.PrivateMessageRepository
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.beans.factory.annotation.Autowired
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
     * 支持游标分页、增量同步、幂等性
     */
    @Transactional(readOnly = true)
    fun receiverSyncOfflineMessages(input: ReceiverOfflineMessageSyncInputDTO): ReceiverOfflineMessageSyncDTO {
        val userId = input.userId
        val targetId = input.targetId
        val limit = if (input.limit in 1..100) input.limit else 50

        DarcyLogger.info("接收方离线同步: userId=$userId, targetId=$targetId, lastMsgId=${input.lastMsgId}")

        // 1. 确定同步起始点（游标策略）
        val messages = if (!input.lastMsgId.isNullOrEmpty()) {
            // 策略1：基于消息ID游标
            privateMessageRepository.findUnreadMessagesWithCursor(
                userId, targetId, input.lastMsgId, PageRequest.of(0, limit)
            )
        } else if (!input.lastSyncTime.isNullOrEmpty()) {
            // 策略2：基于时间戳
            val sinceTime = TimeUtil.parseStringToDateTime(input.lastSyncTime)
            privateMessageRepository.findMessagesSinceTime(
                userId, targetId, sinceTime, PageRequest.of(0, limit)
            )
        } else {
            // 策略3：全量拉取未读消息
            messageReadStatusRepository.receiverFindUnreadMessageListByConversation(userId, targetId)
                .mapNotNull { status ->
                    privateMessageRepository.findById(status.msgId.toLongOrNull() ?: 0).orElse(null)
                }
                .take(limit)
        }

        // 2. 构建响应
        val messageDTOs = messages.map { it.toDTO() }
        val hasMore = messages.size == limit
        val nextCursor = if (hasMore && messages.isNotEmpty()) {
            messages.last().msgId
        } else null

        // 3. 获取已读状态映射
        val msgIds = messages.map { it.msgId }
        val readStatusMap = if (msgIds.isNotEmpty()) {
            messageReadStatusRepository.receiverFindByUserIdAndMsgIdList(userId, msgIds)
                .associate { it.msgId to it.isRead }
        } else emptyMap()

        // 4. 统计未读数
        val unreadCount = messageReadStatusRepository.receiverFindUnreadMessageListByConversation(userId, targetId).size

        DarcyLogger.info("离线同步完成: count=${messageDTOs.size}, hasMore=$hasMore, unreadCount=$unreadCount")

        return ReceiverOfflineMessageSyncDTO(
            messages = messageDTOs,
            hasMore = hasMore,
            nextCursor = nextCursor,
            syncTime = TimeUtil.getCurrentTimeString(),
            unreadCount = unreadCount,
            readStatusMap = readStatusMap,
            conflictMessages = emptyList()
        )
    }

    /**
     * 智能同步：拉取消息并自动标记为已读（原子操作）
     * 适用于客户端希望"拉取即已读"的场景
     */
    @Transactional
    fun receiverSyncAndMarkAsRead(
        userId: Long,
        targetId: Long,
        lastMsgId: String?,
        limit: Int = 50
    ): ReceiverOfflineMessageSyncDTO {
        // 1. 先拉取消息
        val syncResult = receiverSyncOfflineMessages(
            ReceiverOfflineMessageSyncInputDTO(
                userId = userId,
                targetId = targetId,
                lastMsgId = lastMsgId,
                limit = limit
            )
        )

        // 2. 批量标记为已读
        if (syncResult.messages.isNotEmpty()) {
            val msgIds = syncResult.messages.map { it.msgId }
            receiverMarkMessagesAsRead(userId, msgIds)

            DarcyLogger.info("同步并标记已读: userId=$userId, count=${msgIds.size}")
        }

        return syncResult
    }
}
