package com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.`in`

import java.security.Principal

/**
 * stomp 用户唯一标识 username
 */
class UserNamePrincipal(private val name: String) : Principal {
    override fun getName(): String {
        return name
    }
}