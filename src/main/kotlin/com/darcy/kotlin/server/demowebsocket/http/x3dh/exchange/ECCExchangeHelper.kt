package com.darcy.kotlin.server.demowebsocket.http.x3dh.exchange

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr
import com.darcy.kotlin.server.demowebsocket.utils.keyToString
import java.security.*
import javax.crypto.KeyAgreement


object ECCExchangeHelper {
    // 初始化 指定使用 X25519 曲线 （密钥长度固定为 256 位）
    const val ALGORITHM: String = "X25519"

    fun generateKeyPair(): KeyPair {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM)
            // keyPairGenerator.initialize(256) // 不支持手动设置密钥长度
            return keyPairGenerator.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    fun getSharedSecret(privateKey: PrivateKey?, publicKey: PublicKey?): ByteArray {
        try {
            DarcyLogger.info("getSharedSecret: privateKey=${privateKey?.keyToString()}")
            DarcyLogger.info("getSharedSecret: publicKey=${publicKey?.keyToString()}")
            val keyAgreement = KeyAgreement.getInstance(ALGORITHM)
            keyAgreement.init(privateKey)
            keyAgreement.doPhase(publicKey, true)
            val sharedKey =  keyAgreement.generateSecret()
            DarcyLogger.info("getSharedSecret: sharedKey=${sharedKey.bytesToHexStr()}")
            return sharedKey
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        }
    }

}