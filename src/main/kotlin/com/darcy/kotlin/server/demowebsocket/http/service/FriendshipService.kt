package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.friend.Friendship
import com.darcy.kotlin.server.demowebsocket.http.repository.FriendshipRepository
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class FriendshipService @Autowired constructor(
    private val friendshipRepository: FriendshipRepository,
    private val userService: UserService,
    @Lazy
    private val friendRequestService: FriendRequestService,
    private val helloMessageService: HelloMessageService,
    private val readStatusService: MessageReadStatusService,
    private val privateMessageService: PrivateMessageService,
    @Lazy
    private val conversationService: ConversationService,
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

    @Transactional
    fun deleteFriendship(userId: Long, friendId: Long): String {
        val friendshipDeleteCount = friendshipRepository.deleteByUserIdAndFriendId(userId, friendId)
        DarcyLogger.info("friendshipDeleteCount-->$friendshipDeleteCount")
        val friendRequestDeleteCount = friendRequestService.deleteByUserIdAndFriendId(userId, friendId)
        println("friendRequestDeleteCount-->$friendRequestDeleteCount")
        val helloMessageDeleteCount = helloMessageService.deleteByUserIdAndFriendId(userId, friendId)
        println("helloMessageDeleteCount-->$helloMessageDeleteCount")
        val readStatusDeleteCount = readStatusService.deleteByUserIdAndTargetId(userId, friendId)
        println("readStatusDeleteCount-->$readStatusDeleteCount")
        val privateMessageDeleteCount = privateMessageService.deleteByUserIdAndFriendId(userId, friendId)
        println("privateMessageDeleteCount-->$privateMessageDeleteCount")
        val conversationDeleteCount = conversationService.deleteByUserIdAndTargetId(userId, friendId)
        println("conversationDeleteCount-->$conversationDeleteCount")
        return if (friendshipDeleteCount > 0 && friendRequestDeleteCount > 0
            && helloMessageDeleteCount > 0 && readStatusDeleteCount > 0
            && privateMessageDeleteCount >= 0
            && conversationDeleteCount >= 0
        ) {
            "删除成功"
        } else {
            "删除失败"
        }
    }
}