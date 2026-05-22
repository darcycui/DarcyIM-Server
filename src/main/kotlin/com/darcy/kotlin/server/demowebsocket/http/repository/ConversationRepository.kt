package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface ConversationRepository : JpaRepository<Conversation, Long> {

    fun findByUserIdAndConversationTypeAndTargetId(
        userId: Long,
        conversationType: Conversation.ConversationType,
        targetId: Long
    ): Conversation?

    fun findByUserId(userId: Long): List<Conversation>

    @Modifying
    @Query(
        "DELETE FROM Conversation c WHERE (c.user.id = :userId AND c.targetId = :targetId) " +
                "OR (c.user.id = :targetId AND c.targetId = :userId)"
    )
    fun deleteByUserIdAndTargetId(userId: Long, targetId: Long): Int

}