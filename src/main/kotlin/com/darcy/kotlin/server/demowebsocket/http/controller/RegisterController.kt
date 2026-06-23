package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IRegisterApi
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.RegisterRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.UserDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.http.service.RegisterService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logI
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class RegisterController @Autowired constructor(
    private val registerService: RegisterService,
) : IRegisterApi {
    override fun register(params: RegisterRequestDTO): UserDTO {

        val createdAt = TimeUtil.getCurrentTimeDate()
        logI("-->createdAt: $createdAt")

        val userEntity = User(
            username = params.username,
            passwordHash = params.password,
            nickname = params.nickname ,
            avatar = params.avatar,
            phone = params.phone ,
            email = params.email,
            gender = params.gender,
            signature = params.signature,
            status = User.UserStatus.NORMAL,
            onlineStatus = User.OnlineStatus.ONLINE,
            lastActiveTime = LocalDateTime.now(),
            deletedAt = null,
            settings = emptyMap(),
            roles = params.roles,
            token = ""
        )
        userEntity.createdAt = createdAt
        userEntity.updatedAt = createdAt
        val user = registerService.register(userEntity)
        return user.toDTO()
    }
}