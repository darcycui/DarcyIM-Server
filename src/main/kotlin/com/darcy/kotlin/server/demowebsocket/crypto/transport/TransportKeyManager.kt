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
    private lateinit var DHService : DHService
    fun getTransportKey(userId: Long): ByteArray {
        val key = transportKeyMap[userId] ?: run {
//            val existKey = transportService.queryDHKeyExchange(userId).sharedSecret.hexStrToBytes()
//            transportKeyMap[userId] = existKey
//            existKey
            byteArrayOf()
        }
        DarcyLogger.debug("$TAG Get transport key for user $userId: ${key.bytesToHexStr()}")
        return key
    }

    fun setTransportKey(userId: Long, key: ByteArray) {
        transportKeyMap[userId] = key
        DarcyLogger.debug("$TAG Set transport key for user $userId: ${key.bytesToHexStr()}")
    }
    fun removeTransportKey(userId: Long) {
        transportKeyMap.remove(userId)
    }
    fun clear() {
        transportKeyMap.clear()
    }
}