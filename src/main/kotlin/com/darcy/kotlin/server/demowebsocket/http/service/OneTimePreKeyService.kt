package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.OneTimePreKeyInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.OneTimePreKey
import com.darcy.kotlin.server.demowebsocket.exception.code1000.X3DHException
import com.darcy.kotlin.server.demowebsocket.http.repository.IdentityKeyRepository
import com.darcy.kotlin.server.demowebsocket.http.repository.OneTimePreKeyRepository
import com.darcy.kotlin.server.demowebsocket.http.repository.SignedPreKeyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OneTimePreKeyService @Autowired constructor(
    private val identityKeyRepository: IdentityKeyRepository,
    private val signedPreKeyRepository: SignedPreKeyRepository,
    private val oneTimePreKeyRepository: OneTimePreKeyRepository,
    private val userService: UserService,
) {
    fun createOneTimePreKeys(userId: Long, publicKeys: List<OneTimePreKeyInputDTO>): List<OneTimePreKey> {
        val user = userService.queryUserById(userId)
        val oneTimePreKeys = publicKeys.map {
            OneTimePreKey(
                keyId = it.keyId,
                user = user,
                publicKey = it.publicKey
            )
        }
        return oneTimePreKeyRepository.saveAll(oneTimePreKeys)
    }

    fun queryByUserId(userId: Long): List<OneTimePreKey> {
        return oneTimePreKeyRepository.findByUserId(userId)
    }

    fun queryByKeyId(keyId: Long): OneTimePreKey? {
        return oneTimePreKeyRepository.findById(keyId).orElse(null)
    }

    fun queryFirstEnabled(userId: Long): OneTimePreKey {
        return oneTimePreKeyRepository.findFirstEnabled(userId) ?: throw X3DHException.ONE_TIME_PRE_KEY_NOT_EXIST
    }
}