package com.darcy.kotlin.server.demowebsocket.utils

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logW
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordUtil() {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    fun encode(rawPassword: String): String {
        if (rawPassword.isEmpty()) {
            return ""
        }
        logW("original password: $rawPassword")
        return passwordEncoder.encode(rawPassword).also {
            logW("encoded password: $it")
        }
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}