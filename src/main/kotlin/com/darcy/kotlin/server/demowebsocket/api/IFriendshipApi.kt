package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.FriendshipDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.CommonRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.FriendshipDeleteRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody

@RequestMapping("/api/friendships")
interface IFriendshipApi {
    @PostMapping("/query/all")
    fun queryFriendships(@RequestBody @Valid params: CommonRequestDTO): List<FriendshipDTO>

    @PostMapping("/delete")
    fun deleteFriendship(@RequestBody @Valid params: FriendshipDeleteRequestDTO): StringDTO

}