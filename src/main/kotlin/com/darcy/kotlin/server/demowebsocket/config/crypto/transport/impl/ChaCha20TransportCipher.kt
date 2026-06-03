package com.darcy.kotlin.server.demowebsocket.config.crypto.transport.impl

import com.darcy.kotlin.server.demowebsocket.config.crypto.transport.ITransportCipher
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object ChaCha20TransportCipher : ITransportCipher {

    private const val ALGORITHM = "ChaCha20"
    private const val NONCE_SIZE_BYTES = 12 // ChaCha20 标准 Nonce 大小为 96位 (12字节)

    private val secureRandom = SecureRandom()

    override fun encrypt(
        userId: Long,
        plaintext: ByteArray,
        key: ByteArray,
        nonce: ByteArray,
        aad: ByteArray
    ): ByteArray {
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

    override fun decrypt(
        userId: Long,
        ciphertext: ByteArray,
        key: ByteArray,
        aad: ByteArray
    ): ByteArray {
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