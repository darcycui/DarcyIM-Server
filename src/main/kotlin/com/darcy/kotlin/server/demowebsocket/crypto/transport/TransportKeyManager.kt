package com.darcy.kotlin.server.demowebsocket.crypto.transport

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr

object TransportKeyManager {
    private const val TAG = "TransportKeyManager"
    private val transportKeyMap = mutableMapOf<Long, ByteArray>()
    fun getTransportKey(userId: Long): ByteArray {
        val key = transportKeyMap[userId] ?: byteArrayOf()
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