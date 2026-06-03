package com.darcy.kotlin.server.demowebsocket.config.crypto.transport

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.security.SecureRandom

// 定义传输加密解密的接口
interface TransportCipher1 {
    fun encrypt(plaintext: ByteArray): ByteArray
    fun decrypt(ciphertext: ByteArray): ByteArray
}

class ChaCha20TransportCipher(private val key: ByteArray) : TransportCipher1 {

    companion object {
        private const val ALGORITHM = "ChaCha20"
        private const val NONCE_SIZE_BYTES = 12 // ChaCha20 标准 Nonce 大小为 96位 (12字节)
    }

    init {
        require(key.size == 32) { "ChaCha20 key must be 256 bits (32 bytes) long." }
    }

    private val secureRandom = SecureRandom()

    override fun encrypt(plaintext: ByteArray): ByteArray {
        // 1. 生成随机的 12字节 Nonce
        val nonce = ByteArray(NONCE_SIZE_BYTES)
        secureRandom.nextBytes(nonce)

        // 2. 初始化 Cipher
        val cipher = Cipher.getInstance(ALGORITHM)
        val keySpec = SecretKeySpec(key, ALGORITHM)
        val ivSpec = IvParameterSpec(nonce)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        // 3. 加密数据
        val encryptedData = cipher.doFinal(plaintext)

        // 4. 将 Nonce 附加到密文前面
        // 格式: [Nonce (12字节)] + [Encrypted Data]
        // 解密时需要用到这个 Nonce
        return nonce + encryptedData
    }

    override fun decrypt(ciphertext: ByteArray): ByteArray {
        // 1. 检查密文长度是否合法
        require(ciphertext.size >= NONCE_SIZE_BYTES) { "Ciphertext too short to contain Nonce." }

        // 2. 提取 Nonce 和实际的加密数据
        val nonce = ciphertext.copyOfRange(0, NONCE_SIZE_BYTES)
        val encryptedData = ciphertext.copyOfRange(NONCE_SIZE_BYTES, ciphertext.size)

        // 3. 初始化 Cipher
        val cipher = Cipher.getInstance(ALGORITHM)
        val keySpec = SecretKeySpec(key, ALGORITHM)
        val ivSpec = IvParameterSpec(nonce)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        // 4. 解密数据
        return cipher.doFinal(encryptedData)
    }
}

// --- 测试代码 ---
fun main() {
    // 1. 生成一个随机的 32字节 (256位) 密钥
    val key = ByteArray(32).also { SecureRandom().nextBytes(it) }

    // 2. 实例化加密器
    val cipher: TransportCipher1 = ChaCha20TransportCipher(key)

    val originalText = "Hello, CodeGeeX! This is a secret message using ChaCha20."
    println("原始文本: $originalText")

    // 3. 加密
    val plaintext = originalText.toByteArray(Charsets.UTF_8)
    val ciphertext = cipher.encrypt(plaintext)
    println("加密后 (Base64): ${java.util.Base64.getEncoder().encodeToString(ciphertext)}")

    // 4. 解密
    val decryptedText = cipher.decrypt(ciphertext)
    println("解密后: ${String(decryptedText, Charsets.UTF_8)}")

    // 验证
    assert(plaintext.contentEquals(decryptedText))
    println("✅ 加密解密验证通过！")
}
