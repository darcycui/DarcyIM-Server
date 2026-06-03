package com.darcy.kotlin.server.demowebsocket.config.crypto.transport

object TransportKeyManager {
    private val transportKeyMap = mutableMapOf<Long, ByteArray>()
    fun getTransportKey(userId: Long): ByteArray {
        return transportKeyMap[userId] ?: byteArrayOf()
    }

    fun setTransportKey(userId: Long, key: ByteArray) {
        transportKeyMap[userId] = key
    }
    fun removeTransportKey(userId: Long) {
        transportKeyMap.remove(userId)
    }
    fun clear() {
        transportKeyMap.clear()
    }
}