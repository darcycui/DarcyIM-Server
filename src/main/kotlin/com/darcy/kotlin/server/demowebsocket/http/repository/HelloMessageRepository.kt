package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.HelloMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface HelloMessageRepository : JpaRepository<HelloMessage, Long> {
    @Query("SELECT h FROM HelloMessage h WHERE h.fromUser.id = :fromUserId AND h.toUser.id = :toUserId")
    fun findByUserId(fromUserId: Long, toUserId: Long): HelloMessage?

    @Modifying
    @Query("DELETE FROM HelloMessage h WHERE (h.fromUser.id = :userId AND h.toUser.id = :friendId) " +
            "OR (h.fromUser.id= :friendId AND h.toUser.id = :userId)")
    fun deleteByUserIdAndFriendId(userId: Long, friendId: Long): Int
}