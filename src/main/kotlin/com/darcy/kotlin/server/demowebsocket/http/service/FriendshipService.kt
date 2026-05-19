package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.friend.Friendship
import com.darcy.kotlin.server.demowebsocket.http.repository.FriendshipRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FriendshipService @Autowired constructor(
    val friendshipRepository: FriendshipRepository,
    val userService: UserService
) {
    @Transactional
    fun createFriendship(userId: Long, friendId: Long): List<Friendship> {
        val user = userService.queryUserById(userId)
        val friend = userService.queryUserById(friendId)
        val result1 = friendshipRepository.save(
            Friendship(
                user, friend,
                relationStatus = Friendship.RelationStatus.FRIEND
            )
        )
        val result2 = friendshipRepository.save(
            Friendship(
                friend, user,
                relationStatus = Friendship.RelationStatus.FRIEND
            )
        )
        return listOf(result1, result2)
    }

    @Transactional
    fun queryFriendships(userId: Long): List<Friendship> {
        val friends = friendshipRepository.findByUserId(userId)
        return friends
//            .map {
//                it.friend
//            }
    }

    @Transactional
    fun isFriend(userId: Long, friendId: Long): Boolean {
        val result = friendshipRepository.findByUserIdAndFriendId(userId, friendId)
        return result != null
    }
}