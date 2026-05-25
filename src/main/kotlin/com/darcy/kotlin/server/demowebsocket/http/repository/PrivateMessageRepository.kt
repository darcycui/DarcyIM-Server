package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.message.PrivateMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PrivateMessageRepository : JpaRepository<PrivateMessage, Long> {

    @Query(
        "SELECT pm FROM PrivateMessage pm WHERE " +
                "(pm.sender.id = :senderId AND pm.receiver.id = :receiverId) OR " +
                "(pm.sender.id = :receiverId AND pm.receiver.id = :senderId)"
    )
    fun findBothMessagesAll(senderId: Long, receiverId: Long): List<PrivateMessage>

    @Query(
        "SELECT pm FROM PrivateMessage pm WHERE " +
                "(pm.sender.id = :senderId AND pm.receiver.id = :receiverId) OR " +
                "(pm.sender.id = :receiverId AND pm.receiver.id = :senderId) " +
                "ORDER BY pm.sendTime DESC"
    )
    fun findBothMessagesPage(senderId: Long, receiverId: Long, pageable: Pageable): Page<PrivateMessage>

    @Modifying
    @Query(
        "DELETE FROM PrivateMessage pm WHERE " +
                "(pm.sender.id = :userId AND pm.receiver.id = :friendId) OR " +
                "(pm.sender.id = :friendId AND pm.receiver.id = :userId)"
    )
    fun deleteByUserIdAndFriendId(userId: Long, friendId: Long): Int

    // 新增：查询发送给指定用户的未读消息（游标分页）
    @Query(
        "SELECT pm FROM PrivateMessage pm WHERE " +
                "pm.receiver.id = :receiverId " +
                "AND pm.sender.id = :senderId " +
                "AND pm.isRead = false " +
                "AND (:lastMsgId IS NULL OR pm.sendTime > (SELECT pm2.sendTime FROM PrivateMessage pm2 WHERE pm2.msgId = :lastMsgId)) " +
                "ORDER BY pm.sendTime ASC"
    )
    fun findUnreadMessagesWithCursor(
        receiverId: Long,
        senderId: Long,
        lastMsgId: String?,
        pageable: Pageable
    ): List<PrivateMessage>

    // 新增：查询发送给指定用户的消息（按时间范围）
    @Query(
        "SELECT pm FROM PrivateMessage pm WHERE " +
                "pm.receiver.id = :receiverId " +
                "AND pm.sender.id = :senderId " +
                "AND pm.sendTime >= :sinceTime " +
                "ORDER BY pm.sendTime ASC"
    )
    fun findMessagesSinceTime(
        receiverId: Long,
        senderId: Long,
        sinceTime: java.time.LocalDateTime,
        pageable: Pageable
    ): List<PrivateMessage>
}