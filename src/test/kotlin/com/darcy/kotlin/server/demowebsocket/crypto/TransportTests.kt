package com.darcy.kotlin.server.demowebsocket.crypto

import com.darcy.kotlin.server.demowebsocket.crypto.transport.TransportKeyManager
import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.TransportCipherChaCha20
import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.TransportCipherAESGCM
import com.darcy.kotlin.server.demowebsocket.http.service.DHService
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.assertContentEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TransportTests {
    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var dhService: DHService   // 模拟依赖

    @InjectMocks
    private lateinit var keyManager: TransportKeyManager   // 被测类，自动注入 mock

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `test-transport-key-manager`() {
        val originalKey = "1234567890abcdef1234567890abcdef".toByteArray()
        val userId = 100L
        keyManager.setTransportKey(userId, originalKey)
        val key = keyManager.getTransportKey(userId)
        println("key: ${key.toHexString()}")
        assertContentEquals(originalKey, key, "TransportKeyManager 获取密钥失败1")
        keyManager.removeTransportKey(userId)
        val key2 = keyManager.getTransportKey(userId)
        println("key2: ${key2.toHexString()}")
        assertContentEquals(key, key2, "TransportKeyManager 获取密钥失败2")
        keyManager.clear()
        val key3 = keyManager.getTransportKey(userId)
        println("key3: ${key3.toHexString()}")
        assertContentEquals(key, key3, "TransportKeyManager 获取密钥失败3")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `test-chacha20-poly1305-crypto`() {
        keyManager.setTransportKey(1L, "1234567890abcdef1234567890abcdef".toByteArray())
        val message = "hello world".toByteArray()
        println("message: ${message.toHexString()}")

        // ChaCha20-Poly1305 需要 32 字节密钥
        val key = "1234567890abcdef1234567890abcdef".toByteArray()
        println("key: ${key.toHexString()}")

        // Nonce 通常使用 12 字节
        val nonce = "1234567890ab".toByteArray()
        val aad = "additional data".toByteArray()

        val encrypted = TransportCipherChaCha20.encrypt(1, message, key, nonce, aad)
        println("encrypted: ${encrypted.toHexString()}")

        val decrypted = TransportCipherChaCha20.decrypt(1, encrypted, key, aad)
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
        val deprecated = TransportCipherChaCha20.decrypt(1, requestStr.hexStrToBytes(), key, aad)
        println("deprecated: ${deprecated.decodeToString()}") // 68656c6c6f20776f726c64 "hello world"
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `test-aes-gcm-cipher-crypto`() {
        val originalKey = "1234567890abcdef1234567890abcdef".toByteArray()
        val userId = 100L
        keyManager.setTransportKey(userId, originalKey)
        val key = keyManager.getTransportKey(userId)
        println("key: ${key.toHexString()}")
        val message = "hello world".toByteArray()
        println("message: ${message.toHexString()}")
        val nonce = "1234567890ab".toByteArray()
        val aad = "additional data".toByteArray()
        val encrypted = TransportCipherAESGCM.encrypt(userId, message, key, nonce, aad)
        println("encrypted: ${encrypted.toHexString()}")
        val decrypted = TransportCipherAESGCM.decrypt(userId, encrypted, key, aad)
        println("decrypted: ${decrypted.toHexString()}")
        assertContentEquals(message, decrypted, "AES-GCM 加密解密失败")

    }
}