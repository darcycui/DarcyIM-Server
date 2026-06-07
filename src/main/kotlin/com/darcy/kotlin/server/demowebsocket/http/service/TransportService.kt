package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.crypto.transport.TransportKeyManager
import com.darcy.kotlin.server.demowebsocket.domain.table.dh.DHKeyExchange
import com.darcy.kotlin.server.demowebsocket.http.repository.DHKeyExchangeRepository
import com.darcy.kotlin.server.demowebsocket.http.x3dh.exchange.ECCExchangeHelper
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes
import com.darcy.kotlin.server.demowebsocket.utils.keyToString
import com.darcy.kotlin.server.demowebsocket.utils.toPublicKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.KeyPair

var testSharedSecret: String = "" // 测试使用

@Service
class TransportService @Autowired constructor(
    private val dhKeyExchangeRepository: DHKeyExchangeRepository,
    private val userService: UserService,
) {
    fun dhKeyExchange(userId: Long, publicKey: String): DHKeyExchange {
        val user = userService.queryUserById(userId)
        val ephemeralKeyPair: KeyPair = ECCExchangeHelper.generateKeyPair()
        DarcyLogger.info("dhKeyExchange: userId=$userId")
        DarcyLogger.info("dhKeyExchange: 临时私钥=${ephemeralKeyPair.private.keyToString()}")
        DarcyLogger.info("dhKeyExchange: 临时公钥=${ephemeralKeyPair.public.keyToString()}")
        val sharedSecret = ECCExchangeHelper.getSharedSecret(
            ephemeralKeyPair.private,
            publicKey.hexStrToBytes().toPublicKey()
        ).bytesToHexStr()
        testSharedSecret = sharedSecret
        val existItem  = dhKeyExchangeRepository.findByUserId(userId)
        val item = existItem?.apply {
            this.remotePublicKey = publicKey
            this.privateKey = ephemeralKeyPair.private.keyToString()
            this.publicKey = ephemeralKeyPair.public.keyToString()
            this.sharedSecret = sharedSecret // todo 1.内存保存 2.使用 KMS
        }
            ?: DHKeyExchange(
                user = user,
                remotePublicKey = publicKey,
                privateKey = ephemeralKeyPair.private.keyToString(),
                publicKey = ephemeralKeyPair.public.keyToString(),
                sharedSecret = sharedSecret, // todo 1.内存保存 2.使用 KMS
            )
        val result = dhKeyExchangeRepository.save(item)
        TransportKeyManager.setTransportKey(userId, sharedSecret.hexStrToBytes())
        return result
    }

    fun queryDHKeyExchange(userId: Long): DHKeyExchange {
        return dhKeyExchangeRepository.findByUserId(userId) ?: throw Exception("DHKeyExchange not found")
    }
}