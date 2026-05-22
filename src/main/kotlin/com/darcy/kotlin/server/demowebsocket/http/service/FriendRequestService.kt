package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.friend.FriendRequest
import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.http.repository.FriendRequestRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FriendRequestService @Autowired constructor(
    val friendRequestRepository: FriendRequestRepository,
    val userService: UserService,
    val friendshipService: FriendshipService
) {

    @Transactional
    fun createFriendRequest(fromUserId: Long, toUserId: Long, params: Map<String, Any>): FriendRequest {
        val fromUser = userService.queryUserById(fromUserId)
        val toUser = userService.queryUserById(toUserId)
        if (friendshipService.isFriend(fromUserId, toUserId)) {
            throw UserException.FRIENDSHIP_ALREADY_EXIST
        }
        if (friendRequestRepository.findByFromUserId(fromUserId).any {
                it.toUser.id == toUserId && it.status.noNeedRequestFriendAgain()
            }) {
            throw UserException.FRIEND_REQUEST_ALREADY_EXIST
        }
        val friendRequest = FriendRequest(
            fromUser = fromUser,
            toUser = toUser,
            greeting = "你好，我是${fromUser.username}",
            status = FriendRequest.RequestStatus.PENDING,
            remark = "昵称：${toUser.username}",
        ).apply {
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
        val result = friendRequestRepository.save(friendRequest)
        return result
    }

    fun findFriendRequestById(id: Long): FriendRequest {
        val friendRequest = friendRequestRepository.findById(id)
        if (friendRequest.isEmpty) {
            throw UserException.FRIEND_REQUEST_NOT_EXIST
        }
        return friendRequest.get()
    }

    @Transactional
    fun acceptFriendRequest(friendRequestId: Long): FriendRequest {
        val friendRequest = findFriendRequestById(friendRequestId)
        friendRequest.apply {
            status = FriendRequest.RequestStatus.ACCEPTED
            handleTime = LocalDateTime.now()
            handleResult = "已接受"
        }
        val result = friendRequestRepository.save(friendRequest)
        friendshipService.createFriendship(friendRequest.fromUser.id, friendRequest.toUser.id)
        return result
    }

    @Transactional
    fun rejectFriendRequest(friendRequestId: Long): FriendRequest {
        val friendRequest = findFriendRequestById(friendRequestId)
        friendRequest.apply {
            status = FriendRequest.RequestStatus.REJECTED
            handleTime = LocalDateTime.now()
            handleResult = "已拒绝"
        }
        return friendRequestRepository.save(friendRequest)
    }

    @Transactional
    fun ignoreFriendRequest(friendRequestId: Long): FriendRequest {
        val friendRequest = findFriendRequestById(friendRequestId)
        friendRequest.apply {
            status = FriendRequest.RequestStatus.IGNORED
            handleTime = LocalDateTime.now()
            handleResult = "已忽略"
        }
        return friendRequestRepository.save(friendRequest)
    }

    fun queryByFromUserPhone(fromUserId: Long): List<FriendRequest> {
        return friendRequestRepository.findByFromUserId(fromUserId)
    }

    fun queryByToUserId(toUserId: Long): List<FriendRequest> {
        return friendRequestRepository.findByToUserId(toUserId)
    }

    fun deleteByUserIdAndFriendId(userId: Long, friendId: Long): Int {
        return friendRequestRepository.deleteByFromUserIdAndToUserId(userId, friendId)
    }
}