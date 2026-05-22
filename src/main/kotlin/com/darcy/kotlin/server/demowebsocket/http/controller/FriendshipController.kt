package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IFriendshipApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.toDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.FriendshipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class FriendshipController @Autowired constructor(
    private val friendshipService: FriendshipService
) : IFriendshipApi {
    override fun queryFriendships(params: Map<String, String>): String {
        val userId = params["userId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val result = friendshipService.queryFriendships(userId)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }

    override fun deleteFriendship(params: Map<String, String>): String {
        val userId = params["userId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val friendId = params["friendId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("friendId" to "好友ID不能为空"))
        val result = friendshipService.deleteFriendship(userId, friendId)
        return ResultEntity.success(result).toJsonString()
    }
}