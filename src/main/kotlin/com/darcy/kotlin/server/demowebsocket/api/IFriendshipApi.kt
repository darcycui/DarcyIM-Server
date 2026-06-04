package com.darcy.kotlin.server.demowebsocket.api

import com.darcy.kotlin.server.demowebsocket.domain.dto.friend.FriendshipDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.CommonRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.DeleteFriendRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.StringDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/friendships")
interface IFriendshipApi {
    @PostMapping("/query/all")
    fun queryFriendships(@RequestParam params: CommonRequestDTO): List<FriendshipDTO>

    @PostMapping("/delete")
    fun deleteFriendship(@RequestParam params: DeleteFriendRequestDTO): StringDTO

}