package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IUserApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.string.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.user.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class UserController @Autowired constructor(val userService: UserService) : IUserApi {

    override fun createUser(params: Map<String, String>): String {
        val settings = if (params.containsKey("settings")) {
            @Suppress("UNCHECKED_CAST")
            params["settings"] as? Map<String, Any> ?: emptyMap()
        } else {
            emptyMap()
        }

        val createdAt = TimeUtil.parseStringToDateTime(params["createdAt"]?:"")
        DarcyLogger.info("-->createdAt: $createdAt")
        val updatedAt = TimeUtil.parseStringToDateTime(params["updatedAt"]?:"")
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
        return ResultEntity.success(userService.createUser(userEntity).toDTO()).toJsonString()
    }

    override fun updateUser(params: Map<String, String>): String {
        val userBean: User = User(
            username = params["name"].toString()
        )
        return kotlin.runCatching {
            if (contains(userBean).not()) {
                return ResultEntity.error(UserException.USER_NOT_EXIST).toJsonString()
            }
            ResultEntity.success(userService.updateUser(userBean).toDTO()).toJsonString()
        }.onFailure {
            DarcyLogger.error("update user error:", it)
            ResultEntity.error(UserException.USER_UPDATE_ERROR).toJsonString()
        }.onSuccess {
            DarcyLogger.info("update user success")
        }.getOrElse { "update user error" }
    }

    override fun deleteUser(params: Map<String, String>): String {
        val userBean: User = User(
            username = params["name"].toString()
        )
        return kotlin.runCatching {
            if (contains(userBean).not()) {
                return ResultEntity.error(UserException.USER_NOT_EXIST).toJsonString()
            }
            userService.deleteUser(userBean)
            ResultEntity.success("删除用户成功".toDTO()).toJsonString()
        }.onFailure {
            DarcyLogger.error("delete user error:", it)
            return ResultEntity.error(UserException.USER_DELETE_ERROR).toJsonString()
        }.onSuccess {
            DarcyLogger.info("delete user success")
        }.getOrElse { "default delete error" }
    }

    override fun getUserById(params: Map<String, String>): String {
        val idLong = params["id"]?.toLong() ?: throw ParamsException.ParamsNotValid(mapOf("id" to "id不能为空"))
        return ResultEntity.success(userService.queryUserById(idLong).toDTO()).toJsonString()
    }

    override fun getUserByPhone(params: Map<String, String>): String {
        val phone = params["phone"] ?: throw ParamsException.ParamsNotValid(mapOf("phone" to "手机号不能为空"))
        return ResultEntity.success(userService.queryUserByPhone(phone).toDTO()).toJsonString()
    }

    override fun getUserByEmail(params: Map<String, String>): String {
        val email = params["email"] ?: throw ParamsException.ParamsNotValid(mapOf("email" to "邮箱不能为空"))
        return ResultEntity.success(userService.queryUserByEmail(email).toDTO()).toJsonString()
    }

    private fun contains(userBean: User): Boolean {
        val users = userService.queryAllUsers()
        return users.isNotEmpty() && users.contains(userBean)
    }

    override fun getAllUsers(): String {
        val users = userService.queryAllUsers()
        return ResultEntity.success(users.toDTO()).toJsonString()
    }
}