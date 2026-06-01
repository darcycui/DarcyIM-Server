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

    // 重构：返回 Page 类型的消息（按时间范围）
    @Query(
        "SELECT pm FROM PrivateMessage pm WHERE " +
                "pm.receiver.id = :receiverId " +
                "AND pm.sender.id = :senderId " +
                "AND pm.sendTime >= :sinceTime " +
                "ORDER BY pm.sendTime ASC"
    )
    fun findMessagesSinceTimestamp(
        receiverId: Long,
        senderId: Long,
        sinceTime: java.time.LocalDateTime,
        pageable: Pageable
    ): Page<PrivateMessage>

    @Query(
        "SELECT pm FROM PrivateMessage pm " +
                "WHERE pm.msgId IN :unreadMsgIds " +
                "ORDER BY pm.sendTime DESC"
    )
    fun findAllByMsgIdList(unreadMsgIds: List<String>, pageable: Pageable): Page<PrivateMessage>
}