package com.darcy.kotlin.server.demowebsocket.crypto.transport.websocket

import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.TransportCipherAESGCM
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes

object WebsocketCrypto {
    fun encrypt(
        userId: Long,
        plaintext: String,
        url: String
    ): String {
        val aad = "WS:$url".toByteArray()
        return TransportCipherAESGCM.encrypt(userId, plaintext = plaintext.toByteArray(), aad = aad).bytesToHexStr()
    }

    fun decrypt(
        userId: Long,
        ciphertext: String,
        url: String
    ): String {
        val aad = "WS:$url".toByteArray()
        return TransportCipherAESGCM.decrypt(userId, ciphertext.hexStrToBytes(),aad = aad).decodeToString()
    }
}