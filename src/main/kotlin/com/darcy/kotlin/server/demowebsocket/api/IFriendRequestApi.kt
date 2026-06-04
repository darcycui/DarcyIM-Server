package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.FriendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestActionRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestCreateRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestQueryFromRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendRequestQueryToRequestDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/friend-requests")
interface IFriendRequestApi {
    @PostMapping("/create")
    fun createFriendRequest(@RequestBody @Valid params: FriendRequestCreateRequestDTO): FriendRequestDTO

    @PostMapping("/accept")
    fun acceptFriendRequest(@RequestBody @Valid params: FriendRequestActionRequestDTO): FriendRequestDTO

    @PostMapping("/reject")
    fun rejectFriendRequest(@RequestBody @Valid params: FriendRequestActionRequestDTO): FriendRequestDTO

    @PostMapping("/ignore")
    fun ignoreFriendRequest(@RequestBody @Valid params: FriendRequestActionRequestDTO): FriendRequestDTO

    @PostMapping("/query/from")
    fun queryFriendRequestByFromUser(@RequestBody @Valid params: FriendRequestQueryFromRequestDTO): List<FriendRequestDTO>

    @PostMapping("/query/to")
    fun queryFriendRequestByToUser(@RequestBody @Valid params: FriendRequestQueryToRequestDTO): List<FriendRequestDTO>
}
