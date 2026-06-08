package com.darcy.kotlin.server.demowebsocket.crypto.transport.websocket

import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes

object WebsocketCrypto {
    fun encrypt(
        userId: Long,
        plaintext: String,
        url: String
    ): String {
        val aad = "WS:$url".toByteArray()
        return ChaCha20TransportCipher.encrypt(userId, plaintext = plaintext.toByteArray(), aad = aad).bytesToHexStr()
    }

    fun decrypt(
        userId: Long,
        ciphertext: String,
        url: String
    ): String {
        val aad = "WS:$url".toByteArray()
        return ChaCha20TransportCipher.decrypt(userId, ciphertext.hexStrToBytes(),aad = aad).decodeToString()
    }
}