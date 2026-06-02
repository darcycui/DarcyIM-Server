package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.http.repository.UserRepository
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

/**
 * 自定义 web-security 用户认证服务
 * 继承 UserDetailsService
 */
@Service
class CustomUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository
) : UserDetailsService {

    // 重写 web-security 认证方法
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            DarcyLogger.error("用户名不能为空")
            throw UserException.USER_NAME_PASSWORD_EMPTY
        }
        val user = userRepository.findByUsername(username)
        if (user == null) {
            DarcyLogger.error("用户不存在:${username}")
            throw UserException.USER_NOT_EXIST
        }
        DarcyLogger.info("用户存在: ${user.username}")
        user.roles = "USER"
        // 创建 web-security 认证用户 UserDetails
        return User.builder().username(user.username)
            .password(user.passwordHash)
            .roles(user.roles)
            .disabled(false)
            .build()
    }
}