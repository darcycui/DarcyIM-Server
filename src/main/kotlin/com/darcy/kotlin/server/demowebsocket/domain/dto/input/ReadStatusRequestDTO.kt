package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ReceiverMessageReadStatusMarkRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    val userId: Long = 0L,

    @field:NotBlank(message = "发送者ID不能为空")
    val fromUserName: String = "",

    @field:NotBlank(message = "接受者ID不能为空")
    val targetId: Long = 0L,

    @field:NotBlank(message = "接受者名称不能为空")
    val targetName: String = "",

    val msgIds: List<String> = listOf(),

    @field:NotBlank(message = "会话ID不能为空")
    val conversationId: Long = 1,

    @field:NotBlank(message = "会话类型不能为空")
    val conversationType: Int = 1,

    val clientType: String = "",
    val deviceId: String = ""
) : IRequestDTO

/**
 * 接收方离线消息同步请求
 */
data class ReceiverOfflineMessageSyncRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    val userId: Long = 0L,                    // 接收方用户ID

    @field:NotBlank(message = "目标ID不能为空")
    val targetId: Long = 0L,                  // 发送方用户ID（会话目标）

    @field:NotBlank(message = "会话ID不能为空")
    val conversationId: Long = 1,             // 会话ID

    @field:NotBlank(message = "会话类型不能为空")
    val conversationType: Int = 1,            // 会话类型：1-私聊，2-群聊

    val lastMsgId: String? = null,            // 客户端最后一条消息ID（游标）
    val lastSyncTime: String? = null,         // 客户端最后同步时间（备选游标）
    val page: Int? = null,                    // 页码（从0开始）
    val size: Int? = null,                    // 每页大小
    val limit: Int = 50,                      // 单次拉取数量限制（兼容旧版本）
    val deviceId: String = "",                // 设备ID（多端同步）
    val clientType: String = ""               // 客户端类型：WEB/IOS/ANDROID
) : IRequestDTO

/**
 * 接收方离线消息同步请求
 */
data class SenderOfflineMessageReadSyncRequestDTO(
    @field:NotNull(message = "用户ID不能为空")
    val userId: Long = 0L,                    // 接收方用户ID

    @field:NotNull(message = "目标ID不能为空")
    val targetId: Long = 0L,                  // 发送方用户ID（会话目标）

    @field:NotNull(message = "会话ID不能为空")
    val conversationId: Long = 1,             // 会话ID

    @field:NotNull(message = "会话类型不能为空")
    val conversationType: Int = 1,            // 会话类型：1-私聊，2-群聊

    val deviceId: String = "",                // 设备ID（多端同步）
    val clientType: String = "",               // 客户端类型：WEB/IOS/ANDROID
    val since: String = "",                    //  开始时间
    val until: String = ""                     //  结束时间
) : IRequestDTO