package com.darcy.kotlin.server.demowebsocket.crypto.transport

import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.utils.RandomUtil
import java.security.SecureRandom

// 定义传输加密解密的接口
interface ITransportCipher {
    fun encrypt(
        userId: Long,
        plaintext: ByteArray,
        key: ByteArray = TransportKeyManager.getTransportKey(userId),
        nonce: ByteArray = RandomUtil.secureRandomIV(12),
        aad: ByteArray
    ): ByteArray

    fun decrypt(
        userId: Long,
        ciphertext: ByteArray,
        key: ByteArray = TransportKeyManager.getTransportKey(userId),
        aad: ByteArray
    ): ByteArray
}
