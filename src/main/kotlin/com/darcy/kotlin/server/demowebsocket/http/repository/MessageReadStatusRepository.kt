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

    @Query("SELECT mrs FROM MessageReadStatus mrs WHERE mrs.targetId = :userId AND mrs.msgId = :msgId")
    fun findByUserIdAndMsgId(userId: Long, msgId: String): MessageReadStatus?

    @Query("SELECT mrs FROM MessageReadStatus mrs WHERE mrs.targetId = :userId AND mrs.msgId IN :msgIds")
    fun findByUserIdAndMsgIds(userId: Long, msgIds: List<String>): List<MessageReadStatus>

    @Query("SELECT mrs FROM MessageReadStatus mrs WHERE mrs.user.id = :userId AND mrs.conversationType = 'PRIVATE' AND mrs.targetId = :targetId AND mrs.isRead = false ORDER BY mrs.readTime ASC")
    fun findUnreadMessagesByConversation(userId: Long, targetId: Long): List<MessageReadStatus>

    @Modifying
    @Transactional
    @Query("UPDATE MessageReadStatus mrs SET mrs.isRead = true, mrs.readTime = :readTime " +
            "WHERE mrs.targetId = :userId AND mrs.msgId IN :msgIds")
    fun markMessagesAsRead(userId: Long, msgIds: List<String>, readTime: LocalDateTime): Int

    @Modifying
    @Query("DELETE FROM MessageReadStatus mrs WHERE (mrs.user.id = :userId AND mrs.targetId = :friendId) " +
            "OR (mrs.user.id = :friendId AND mrs.targetId = :userId)")
    fun deleteByUserIdAndTargetId(userId: Long, friendId: Long): Int
}
