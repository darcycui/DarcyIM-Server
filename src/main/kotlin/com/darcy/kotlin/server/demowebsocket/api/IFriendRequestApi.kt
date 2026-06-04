package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.FriendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestActionRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestQueryFromRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestQueryToRequestDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/friend-requests")
interface IFriendRequestApi {
    @PostMapping("/create")
    fun createFriendRequest(@RequestBody params: FriendRequestCreateRequestDTO): FriendRequestDTO

    @PostMapping("/accept")
    fun acceptFriendRequest(@RequestBody params: FriendRequestActionRequestDTO): FriendRequestDTO

    @PostMapping("/reject")
    fun rejectFriendRequest(@RequestBody params: FriendRequestActionRequestDTO): FriendRequestDTO

    @PostMapping("/ignore")
    fun ignoreFriendRequest(@RequestBody params: FriendRequestActionRequestDTO): FriendRequestDTO

    @PostMapping("/query/from")
    fun queryFriendRequestByFromUser(@RequestBody params: FriendRequestQueryFromRequestDTO): List<FriendRequestDTO>

    @PostMapping("/query/to")
    fun queryFriendRequestByToUser(@RequestBody params: FriendRequestQueryToRequestDTO): List<FriendRequestDTO>
}
