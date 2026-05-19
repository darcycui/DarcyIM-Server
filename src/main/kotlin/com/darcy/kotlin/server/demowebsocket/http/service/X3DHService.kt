package com.darcy.kotlin.server.demowebsocket.http.service

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.OneTimePreKeyInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.X3DHKeysPullDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.X3DHKeysPushDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.HelloMessage
import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.exception.code1000.X3DHException
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class X3DHService @Autowired constructor(
    private val identityKeyService: IdentityKeyService,
    private val signedPreKeyService: SignedPreKeyService,
    private val oneTimePreKeyService: OneTimePreKeyService,
    private val userService: UserService,
    private val helloMessageService: HelloMessageService
) {
    companion object {
        const val KEY_IDENTITY_KEY = "identityKey"
        const val KEY_SIGNED_PRE_KEY = "signedPreKey"
        const val KEY_ONE_TIME_PRE_KEY = "oneTimePreKey"
        const val KEY_EPHEMERAL_KEY = "ephemeralKey"
    }

    @Transactional
    fun createUserKeys(
        userId: Long,
        identityKeyStr: String,
        signedPreKeyStr: String,
        oneTimePreKeysStr: String
    ): X3DHKeysPushDTO {
        if (userService.isUserExistById(userId).not()) {
            throw UserException.USER_NOT_EXIST
        }
        val identityKey = identityKeyService.createIdentityKey(userId, identityKeyStr)
        val signedPreKey = signedPreKeyService.createSignedPreKey(userId, signedPreKeyStr)
        val oneTimePreKeyList = try {
            JSON.parseArray(oneTimePreKeysStr,  OneTimePreKeyInputDTO::class.java)
        } catch (e: Exception) {
            DarcyLogger.error("解析 oneTimePreKeys 失败: ${e.message}", e)
            emptyList()
        }
        val oneTimePreKeys = oneTimePreKeyService.createOneTimePreKeys(userId, oneTimePreKeyList)
        val status =
            if (identityKey.publicKey.isNotEmpty() && signedPreKey.publicKey.isNotEmpty() && oneTimePreKeys.isNotEmpty()) {
                1
            } else {
                0
            }
        return X3DHKeysPushDTO(
            userId = userId,
            status = status,
            message = "success",
        )
    }


    fun queryUserKeys(aliceUserId: Long, bobUserId: Long): X3DHKeysPullDTO {
        if (userService.isUserExistById(aliceUserId).not() || userService.isUserExistById(bobUserId).not()) {
            throw UserException.USER_NOT_EXIST
        }
        val identityKey = identityKeyService.queryByUserId(bobUserId)
        val signedPreKey = signedPreKeyService.queryByUserId(bobUserId)
        val oneTimePreKey = oneTimePreKeyService.queryFirstEnabled(bobUserId)
        return X3DHKeysPullDTO(
            identityKey.publicKey,
            signedPreKey.publicKey,
            oneTimePreKey.publicKey,
            oneTimePreKeyId = oneTimePreKey.keyId
        )
    }

    fun createHelloMessage(
        fromUserId: Long,
        toUserId: Long,
        aliceIdentityKey: String,
        aliceEphemeralKey: String,
        bobOneTimePreKeyId: String
    ): HelloMessage {
        if (userService.isUserExistById(fromUserId).not() || userService.isUserExistById(toUserId).not()) {
            throw UserException.USER_NOT_EXIST
        }
        val fromUser = userService.queryUserById(fromUserId)
        val toUser = userService.queryUserById(toUserId)
        val helloMessage = HelloMessage(
            fromUser = fromUser,
            toUser = toUser,
            aliceIdentityKey = aliceIdentityKey,
            aliceEphemeralKey = aliceEphemeralKey,
            bobOneTimePreKeyId = bobOneTimePreKeyId
        )
        return helloMessageService.createHelloMessage(helloMessage)
    }

    fun queryHelloMessage(fromUserId: Long, toUserId: Long): HelloMessage {
        if (userService.isUserExistById(fromUserId).not() || userService.isUserExistById(toUserId).not()) {
            throw UserException.USER_NOT_EXIST
        }
        val helloMessage = helloMessageService.queryHelloMessage(fromUserId, toUserId)
            ?: throw X3DHException.HELLO_MESSAGE_NOT_EXIST
        return helloMessage
    }
}