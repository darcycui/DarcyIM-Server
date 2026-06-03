package com.darcy.kotlin.server.demowebsocket.config.crypto.transport

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger

object TransportCipher {
    private const val TAG = "TransportCipher"
    private val provider:;

    /**
     * ChaCha20-Poly1305 加密
     * @param content 明文内容
     * @param key 加密密钥（32字节）
     * @param nonce Nonce/IV（12字节推荐）
     * @param aad 附加认证数据（AAD），可选
     * @return 密文（包含认证标签，不包含nonce）
     */
    suspend fun encrypt(
        userId: Long,
        content: ByteArray,
        key: ByteArray = TransportGlobalStorage.getServerDhKey().hexStrToBytes(),
        nonce: ByteArray = RandomHelper.secureRandomIV(12),
        aad: ByteArray
    ): ByteArray {
        return runCatching {
            val chacha20 = provider.get(ChaCha20Poly1305)
            val newKey =
                chacha20.keyDecoder().decodeFromByteArray(
                    ChaCha20Poly1305.Key.Format.RAW, key
                )
            val cipher = newKey.cipher()
            val data = cipher.encryptWithIv(nonce, content, aad ?: byteArrayOf())
            nonce + data
        }.onFailure {
            DarcyLogger.error("$TAG 加密失败: ${it::class.simpleName} ${it.message}")
        }.getOrElse { byteArrayOf() }
    }

    /**
     * ChaCha20-Poly1305 解密
     * @param content 密文内容（包含认证标签）
     * @param key 解密密钥（32字节）
     * @param nonce Nonce/IV（12字节）
     * @param aad 附加认证数据（AAD），可选
     * @return 明文
     */
    suspend fun decrypt(
        userId: Long,
        content: ByteArray,
        key: ByteArray,
        nonce: ByteArray,
        aad: ByteArray
    ): ByteArray {
        return runCatching {
            val chacha20 = provider.get(ChaCha20Poly1305)
            val newKey = chacha20.keyDecoder().decodeFromByteArray(
                ChaCha20Poly1305.Key.Format.RAW, key
            )
            val cipher = newKey.cipher()
            val data = cipher.decryptWithIv(nonce, content, aad ?: byteArrayOf())
            data
        }.onFailure {
            DarcyLogger.error("$TAG 解密失败: ${it::class.simpleName} ${it.message}")
        }.getOrElse { byteArrayOf() }
    }
}