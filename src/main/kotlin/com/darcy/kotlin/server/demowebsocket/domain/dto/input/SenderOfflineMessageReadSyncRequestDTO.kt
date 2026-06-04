package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank


/**
 * 接收方离线消息同步请求
 */
data class SenderOfflineMessageReadSyncRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    val userId: Long = 0L,                    // 接收方用户ID

    @field:NotBlank(message = "目标ID不能为空")
    val targetId: Long = 0L,                  // 发送方用户ID（会话目标）

    @field:NotBlank(message = "会话ID不能为空")
    val conversationId: Long = 1,             // 会话ID

    @field:NotBlank(message = "会话类型不能为空")
    val conversationType: Int = 1,            // 会话类型：1-私聊，2-群聊

    val deviceId: String = "",                // 设备ID（多端同步）
    val clientType: String = "",               // 客户端类型：WEB/IOS/ANDROID
    val since: String = "",                    //  开始时间
    val until: String = ""                     //  结束时间
)