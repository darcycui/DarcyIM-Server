package com.darcy.kotlin.server.demowebsocket.crypto

import com.darcy.kotlin.server.demowebsocket.crypto.transport.TransportKeyManager
import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class TransformTests {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `test-chacha20-poly1305-crypto`() {
        TransportKeyManager.setTransportKey(1L, "1234567890abcdef1234567890abcdef".toByteArray())
        val message = "hello world".toByteArray()
        println("message: ${message.toHexString()}")

        // ChaCha20-Poly1305 需要 32 字节密钥
        val key = "1234567890abcdef1234567890abcdef".toByteArray()
        println("key: ${key.toHexString()}")

        // Nonce 通常使用 12 字节
        val nonce = "1234567890ab".toByteArray()
        val aad = "additional data".toByteArray()

        val encrypted = ChaCha20TransportCipher.encrypt(1, message, key, nonce, aad)
        println("encrypted: ${encrypted.toHexString()}")

        val decrypted = ChaCha20TransportCipher.decrypt(1, encrypted, key, aad)
        println("decrypted: ${decrypted.toHexString()}")

        assertContentEquals(message, decrypted, "ChaCha20-Poly1305 加密解密失败")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `test-chacha20-cipher-decrypt-http-request`() {
        val requestStr = "3132333435363738393061621e563e341d999e023321ef2a1199b809a911c0d5af7b46b5711f4a"
        val key = "1234567890abcdef1234567890abcdef".toByteArray()
        val nonce = "1234567890ab".toByteArray()
        val aad = "additional data".toByteArray()
        val deprecated = ChaCha20TransportCipher.decrypt(1, requestStr.hexStrToBytes(), key, aad)
        println("deprecated: ${deprecated.decodeToString()}") // 68656c6c6f20776f726c64 "hello world"
    }
}