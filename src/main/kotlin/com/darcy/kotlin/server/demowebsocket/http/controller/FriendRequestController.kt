package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IFriendRequestApi
import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.FriendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestActionRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestQueryFromRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestQueryToRequestDTO
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
    override fun createFriendRequest(params: FriendRequestCreateRequestDTO): FriendRequestDTO {
        val result = friendRequestService.createFriendRequest(
            params.fromUserId, params.toUserId
        )
        return result.toDTO()
    }

    override fun acceptFriendRequest(params: FriendRequestActionRequestDTO): FriendRequestDTO {
        val result = friendRequestService.acceptFriendRequest(
            params.friendRequestId
        )
        return result.toDTO()
    }

    override fun rejectFriendRequest(params: FriendRequestActionRequestDTO): FriendRequestDTO {
        val result = friendRequestService.rejectFriendRequest(
            params.friendRequestId
        )
        return result.toDTO()
    }

    override fun ignoreFriendRequest(params: FriendRequestActionRequestDTO): FriendRequestDTO {
        val result = friendRequestService.ignoreFriendRequest(params.friendRequestId)
        return result.toDTO()
    }

    override fun queryFriendRequestByFromUser(params: FriendRequestQueryFromRequestDTO): List<FriendRequestDTO> {
        val result = friendRequestService.queryByFromUserPhone(params.fromUserId)
        return result.toDTO()
    }

    override fun queryFriendRequestByToUser(params: FriendRequestQueryToRequestDTO): List<FriendRequestDTO> {
        val result = friendRequestService.queryByToUserId(params.toUserId)
        return result.toDTO()
    }
}