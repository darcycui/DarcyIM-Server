package com.darcy.kotlin.server.demowebsocket.http.controller

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.api.IRegisterApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.User
import com.darcy.kotlin.server.demowebsocket.exception.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.*
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class RegisterController @Autowired constructor(
    private val registerService: RegisterService,
    private val userService: UserService,
    private val identityKeyService: IdentityKeyService,
    private val signedPreKeyService: SignedPreKeyService,
    private val oneTimePreKeyService: OneTimePreKeyService,
    private val deviceService: DeviceService
) : IRegisterApi {
    override fun register(params: Map<String, String>): String {
        // String 解析为 Map
        val settings = if (params.containsKey("settings")) {
            try {
                @Suppress("UNCHECKED_CAST")
                JSON.parseObject(params["settings"], Map::class.java) as? Map<String, Any> ?: emptyMap()
            } catch (e: Exception) {
                DarcyLogger.error("解析 settings 失败: ${e.message}", e)
                emptyMap()
            }
        } else {
            emptyMap()
        }

        val createdAt = TimeUtil.parseStringToDateTime(params["createdAt"] ?: "")
        DarcyLogger.info("-->createdAt: $createdAt")
        val updatedAt = TimeUtil.parseStringToDateTime(params["updatedAt"] ?: "")
        DarcyLogger.info("-->updatedAt: $updatedAt")

        val userEntity = User(
            username = params["username"]
                ?: throw ParamsException.ParamsNotValid(mapOf("username" to "用户名不能为空")),
            passwordHash = params["password"]
                ?: throw ParamsException.ParamsNotValid(mapOf("password" to "密码不能为空")),
            nickname = params["nickname"] ?: throw ParamsException.ParamsNotValid(mapOf("nickname" to "昵称不能为空")),
            avatar = params["avatar"] ?: throw ParamsException.ParamsNotValid(mapOf("avatar" to "头像不能为空")),
            phone = params["phone"] ?: throw ParamsException.ParamsNotValid(mapOf("phone" to "手机号不能为空")),
            email = params["email"] ?: throw ParamsException.ParamsNotValid(mapOf("email" to "邮箱不能为空")),
            gender = params["gender"] ?: throw ParamsException.ParamsNotValid(mapOf("gender" to "性别不能为空")),
            signature = params["signature"]
                ?: throw ParamsException.ParamsNotValid(mapOf("signature" to "个性签名不能为空")),
            status = User.UserStatus.NORMAL,
            onlineStatus = User.OnlineStatus.ONLINE,
            lastActiveTime = LocalDateTime.now(),
            deletedAt = null,
            settings = settings,
            roles = params["roles"] ?: throw ParamsException.ParamsNotValid(mapOf("roles" to "角色不能为空")),
            token = ""
        )
        userEntity.createdAt = createdAt
        userEntity.updatedAt = updatedAt
        val user = registerService.register(userEntity)
        dealX3DHKeys(params, user)
        return ResultEntity.success(user.toDTO()).toJsonString()
    }

    private fun dealX3DHKeys(params: Map<String, String>, user: User) {
        val identityKey =
            params["identityKey"] ?: throw ParamsException.ParamsNotValid(mapOf("identityKey" to "身份密钥不能为空"))
        val preSignedKey = params["preSignedKey"]
            ?: throw ParamsException.ParamsNotValid(mapOf("preSignedKey" to "预签名密钥不能为空"))
        val oneTimePreKey = params["oneTimePreKey"]
            ?: throw ParamsException.ParamsNotValid(mapOf("oneTimePreKey" to "一次性预钥不能为空"))

        // String 解析为 List
        val oneTimePreKeyList = try {
            JSON.parseArray(params["oneTimePreKeyList"], String::class.java)
        } catch (e: Exception) {
            DarcyLogger.error("解析 oneTimePreKeyList 失败: ${e.message}", e)
            emptyList()
        }
        identityKeyService.createIdentityKey(
            userId = user.id,
            deviceName = "", // todo 先不考虑多设备
            publicKey = identityKey
        )
        signedPreKeyService.createSignedPreKey(
            userId = user.id,
            publicKey = preSignedKey
        )
        oneTimePreKeyService.createOneTimePreKeys(
            userId = user.id,
            publicKeys = oneTimePreKeyList
        )
    }
}