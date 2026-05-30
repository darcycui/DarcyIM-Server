package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.message.MessageReadStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface MessageReadStatusRepository : JpaRepository<MessageReadStatus, Long> {

    @Query("SELECT mrs FROM MessageReadStatus mrs WHERE mrs.user.id = :senderId AND mrs.msgId = :msgId")
    fun senderFindBySenderIdAndMsgId(senderId: Long, msgId: String): MessageReadStatus?

    @Modifying
    @Transactional
    @Query(
        "UPDATE MessageReadStatus mrs SET mrs.isRead = true, mrs.readTime = :readTime " +
                "WHERE mrs.targetId = :receiverId AND mrs.msgId IN :msgIds"
    )
    fun receiverMarkMessagesAsRead(receiverId: Long, msgIds: List<String>, readTime: LocalDateTime): Int

    @Query("SELECT mrs FROM MessageReadStatus mrs WHERE mrs.targetId = :userId AND mrs.msgId IN :msgIds")
    fun receiverFindByUserIdAndMsgIdList(userId: Long, msgIds: List<String>): List<MessageReadStatus>

    @Query(
        "SELECT mrs FROM MessageReadStatus mrs WHERE mrs.user.id = :senderId " +
                "AND mrs.conversationType = 'PRIVATE' " +
                "AND mrs.targetId = :receiverId " +
                "AND mrs.isRead = false " +
                "ORDER BY mrs.readTime ASC"
    )
    fun receiverFindUnreadMessageListByConversation(senderId: Long, receiverId: Long): List<MessageReadStatus>


    @Modifying
    @Query(
        "DELETE FROM MessageReadStatus mrs WHERE (mrs.user.id = :userId AND mrs.targetId = :friendId) " +
                "OR (mrs.user.id = :friendId AND mrs.targetId = :userId)"
    )
    fun deleteByUserIdAndTargetId(userId: Long, friendId: Long): Int

    @Query(
        "SELECT mrs FROM MessageReadStatus mrs WHERE mrs.user.id = :senderId " +
                "AND mrs.conversationType = 'PRIVATE' " +
                "AND mrs.targetId = :receiverId " +
                "AND mrs.isRead = true " +
                "ORDER BY mrs.readTime ASC"
    )
    fun senderFindReadMessageListByConversation(senderId: Long, receiverId: Long): List<MessageReadStatus>

    // 支持时间范围查询离线期间的已读状态
    @Query(
        "SELECT mrs FROM MessageReadStatus mrs WHERE mrs.user.id = :senderId " +
                "AND mrs.conversationType = 'PRIVATE' " +
                "AND mrs.targetId = :receiverId " +
                "AND mrs.isRead = true " +
                "AND mrs.readTime >= :since " +
                "AND mrs.readTime <= :until " +
                "ORDER BY mrs.readTime ASC"
    )
    fun senderFindReadMessageListByConversationWithTimeRange(
        senderId: Long,
        receiverId: Long,
        since: LocalDateTime,
        until: LocalDateTime
    ): List<MessageReadStatus>

    // 查询某用户收到的未读消息ID列表
    @Query(
        "SELECT DISTINCT mrs.msgId FROM MessageReadStatus mrs WHERE " +
                "mrs.targetId = :receiverId AND " +
                "mrs.isRead = false"
    )
    fun findUnreadMsgIdsByReceiver(receiverId: Long): List<String>

    // 查询与特定发送者的未读消息ID列表
    @Query(
        "SELECT DISTINCT mrs.msgId FROM MessageReadStatus mrs WHERE " +
                "mrs.targetId = :receiverId AND " +
                "mrs.user.id = :senderId AND " +
                "mrs.isRead = false"
    )
    fun findUnreadMsgIdsByConversation(receiverId: Long, senderId: Long): List<String>

}
