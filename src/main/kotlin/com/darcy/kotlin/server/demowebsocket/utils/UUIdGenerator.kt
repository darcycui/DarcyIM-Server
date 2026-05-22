package com.darcy.kotlin.server.demowebsocket.utils

import org.springframework.stereotype.Component
import java.util.*

/**
 * 生成唯一 uuid
 * 目前不考虑分布式集群的雪花算法
 */
@Component
class UUIdGenerator {

    companion object {
        private const val USER_PREFIX = "user_"
        private const val CONV_PREFIX = "conv_"
        private const val MSG_PREFIX = "msg_"
        private const val GROUP_PREFIX = "group_"
        private const val GROUP_MSG_PREFIX = "msg_g_"
    }

    fun nextUserId(): String = "$USER_PREFIX${generateRandomUUID()}"
    fun nextConversationId(): String = "$CONV_PREFIX${generateRandomUUID()}"
    fun nextMessageId(): String = "$MSG_PREFIX${generateRandomUUID()}"
    fun nextGroupId(): String = "$GROUP_PREFIX${generateRandomUUID()}"
    fun nextGroupMessageId(): String = "$GROUP_MSG_PREFIX${generateRandomUUID()}"


    /**
     * 生成标准 UUID v4
     */
    @Synchronized
    fun generateRandomUUID(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}