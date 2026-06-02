package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IFriendRequestApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.FriendRequestService
import com.darcy.kotlin.server.demowebsocket.http.service.FriendshipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class FriendRequestController @Autowired constructor(
    private val friendRequestService: FriendRequestService,
    private val friendshipService: FriendshipService,
) : IFriendRequestApi {
    override fun createFriendRequest(params: Map<String, String>): String {
        val fromUserId = params["fromUserId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("fromUserId" to "发起人ID不能为空"))
        val toUserId = params["toUserId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("toUserId" to "目标人ID不能为空")
        )
        val result = friendRequestService.createFriendRequest(fromUserId, toUserId, params)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun acceptFriendRequest(params: Map<String, String>): String {
        val friendRequestId = params["friendRequestId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("friendRequestId" to "好友请求ID不能为空")
        )
        val result = friendRequestService.acceptFriendRequest(friendRequestId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun rejectFriendRequest(params: Map<String, String>): String {
        val friendRequestId = params["friendRequestId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("friendRequestId" to "好友请求ID不能为空")
        )
        val result = friendRequestService.rejectFriendRequest(friendRequestId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun ignoreFriendRequest(params: Map<String, String>): String {
        val friendRequestId = params["friendRequestId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("friendRequestId" to "好友请求ID不能为空")
        )
        val result = friendRequestService.ignoreFriendRequest(friendRequestId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun queryFriendRequestByFromUser(params: Map<String, String>): String {
        val fromUserId = params["fromUserId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("fromUserId" to "用户ID不能为空")
        )
        val result = friendRequestService.queryByFromUserPhone(fromUserId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun queryFriendRequestByToUser(params: Map<String, String>): String {
        val toUserId = params["toUserId"]?.toLongOrNull() ?: throw ParamsException.ParamsNotValid(
            mapOf("toUserId" to "用户ID不能为空")
        )
        val result = friendRequestService.queryByToUserId(toUserId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }
}