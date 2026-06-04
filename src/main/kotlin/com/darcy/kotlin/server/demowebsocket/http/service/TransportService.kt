package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.dh.DHKeyExchange
import com.darcy.kotlin.server.demowebsocket.http.repository.DHKeyExchangeRepository
import com.darcy.kotlin.server.demowebsocket.http.x3dh.exchange.ECCExchangeHelper
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
        val sharedSecret = ECCExchangeHelper.getSharedSecret(
            ephemeralKeyPair.private,
            publicKey.hexStrToBytes().toPublicKey()
        ).bytesToHexStr()
        testSharedSecret = sharedSecret
        val item = dhKeyExchangeRepository.findByUserId(userId)
            ?: DHKeyExchange(
                user = user,
                remotePublicKey = publicKey,
                privateKey = ephemeralKeyPair.private.keyToString(),
                publicKey = ephemeralKeyPair.public.keyToString(),
                sharedSecret = sharedSecret, // todo 1.内存保存 2.使用 KMS
            )
        return dhKeyExchangeRepository.save(item)
    }
}