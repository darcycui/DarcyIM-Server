package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.friend.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FriendshipRepository : JpaRepository<Friendship, Long> {
    fun findByUserId(userId: Long): List<Friendship>

    fun findByUserIdAndFriendId(userId: Long, friendId: Long): Friendship?

    @Modifying
    @Query(
        "DELETE FROM Friendship f WHERE (f.user.id = :userId AND f.friend.id = :friendId) " +
                "OR (f.friend.id = :userId AND f.user.id = :friendId)"
    )
    fun deleteByUserIdAndFriendId(userId: Long, friendId: Long): Int
}