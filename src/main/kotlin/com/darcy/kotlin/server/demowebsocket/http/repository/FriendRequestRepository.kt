package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.friend.FriendRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FriendRequestRepository : JpaRepository<FriendRequest, Long> {
    // 我发起的好友请求
    fun findByFromUserId(fromUserId: Long): List<FriendRequest>

    // 别人发给我的好友请求
    fun findByToUserId(toUserId: Long): List<FriendRequest>

    @Modifying
    @Query(
        "DELETE FROM FriendRequest f WHERE f.fromUser.id = :userId AND f.toUser.id = :friendId " +
                "OR f.fromUser.id = :friendId AND f.toUser.id = :userId"
    )
    fun deleteByFromUserIdAndToUserId(userId: Long, friendId: Long): Int

    // 更新，用于接受、拒绝、忽略好友请求——再次调用save()

}