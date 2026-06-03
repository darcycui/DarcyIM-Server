package com.darcy.kotlin.server.demowebsocket.config.crypto.transport

import com.darcy.kotlin.server.demowebsocket.config.crypto.transport.impl.ChaCha20TransportCipher
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

// --- 测试代码 ---
fun main() {
    // 1. 生成一个随机的 32字节 (256位) 密钥
    val key = ByteArray(32).also { SecureRandom().nextBytes(it) }
    TransportKeyManager.setTransportKey(1, key)
    val aad = "POST:/api/friends".toByteArray(Charsets.UTF_8)

    // 2. 实例化加密器
    val cipher: ITransportCipher = ChaCha20TransportCipher

    val originalText = "Hello, CodeGeeX! This is a secret message using ChaCha20."
    println("原始文本: $originalText")

    // 3. 加密
    val plaintext = originalText.toByteArray(Charsets.UTF_8)
    val ciphertext = cipher.encrypt(1, plaintext, aad = aad)
    println("加密后 (Base64): ${java.util.Base64.getEncoder().encodeToString(ciphertext)}")

    // 4. 解密
    val decryptedText = cipher.decrypt(1, ciphertext, aad = aad)
    println("解密后: ${String(decryptedText, Charsets.UTF_8)}")

    // 验证
    assert(plaintext.contentEquals(decryptedText))
    println("✅ 加密解密验证通过！")
}
