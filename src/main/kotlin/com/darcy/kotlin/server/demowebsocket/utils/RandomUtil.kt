package com.darcy.kotlin.server.demowebsocket.utils

import java.security.SecureRandom

object RandomUtil {
    fun secureRandomIV(bytes: Int): ByteArray {
        val iv = ByteArray(bytes)
        SecureRandom().nextBytes(iv)
        return iv
    }
}