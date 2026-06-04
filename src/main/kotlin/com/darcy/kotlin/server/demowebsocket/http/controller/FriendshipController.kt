package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IFriendshipApi
import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.FriendshipDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.CommonRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendshipDeleteRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.toDTO
import com.darcy.kotlin.server.demowebsocket.http.service.FriendshipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class FriendshipController @Autowired constructor(
    private val friendshipService: FriendshipService
) : IFriendshipApi {
    override fun queryFriendships(params: CommonRequestDTO): List<FriendshipDTO> {
        val result = friendshipService.queryFriendships(params.userId)
        return result.toDTO()
    }

    override fun deleteFriendship(params: FriendshipDeleteRequestDTO): StringDTO {
        val result = friendshipService.deleteFriendship(params.userId, params.friendId)
        return result.toDTO()
    }
}