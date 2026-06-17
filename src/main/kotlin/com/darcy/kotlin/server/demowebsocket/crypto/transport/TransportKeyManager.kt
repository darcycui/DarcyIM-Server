package com.darcy.kotlin.server.demowebsocket.crypto.transport

import com.darcy.kotlin.server.demowebsocket.http.service.DHService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
object TransportKeyManager {
    private const val TAG = "TransportKeyManager"
    private val transportKeyMap = mutableMapOf<Long, ByteArray>()

    @Autowired
    private lateinit var DHService: DHService
    fun getTransportKey(userId: Long): ByteArray {
        if (userId <= 0) {
            DarcyLogger.error("用户ID错误: $userId")
            return byteArrayOf()
        }
        val key = transportKeyMap[userId] ?: run {
//            val existKey = transportService.queryDHKeyExchange(userId).sharedSecret.hexStrToBytes()
//            transportKeyMap[userId] = existKey
//            existKey
            byteArrayOf()
        }
        DarcyLogger.debug("$TAG 获取用户 $userId 的传输密钥: ${key.bytesToHexStr()}")
        return key
    }

    fun setTransportKey(userId: Long, key: ByteArray) {
        if (userId <= 0) {
            DarcyLogger.error("用户ID错误: $userId")
            return
        }
        transportKeyMap[userId] = key
        DarcyLogger.debug("$TAG 设置用户 $userId 的传输密钥: ${key.bytesToHexStr()}")
    }

    fun removeTransportKey(userId: Long) {
        if (userId <= 0) {
            DarcyLogger.error("用户ID错误: $userId")
            return
        }
        transportKeyMap.remove(userId)
    }

    fun clear() {
        transportKeyMap.clear()
    }
}