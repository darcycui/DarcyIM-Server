package com.darcy.kotlin.server.demowebsocket.domain.dto.input


/**
 * 接收方离线消息同步请求
 */
data class ReceiverOfflineMessageSyncInputDTO(
    val userId: Long = 0L,                    // 接收方用户ID
    val targetId: Long = 0L,                  // 发送方用户ID（会话目标）
    val conversationType: Int = 1,            // 会话类型：1-私聊，2-群聊
    val lastMsgId: String? = null,            // 客户端最后一条消息ID（游标）
    val lastSyncTime: String? = null,         // 客户端最后同步时间（备选游标）
    val limit: Int = 50,                      // 单次拉取数量限制
    val deviceId: String = "",                // 设备ID（多端同步）
    val clientType: String = ""               // 客户端类型：WEB/IOS/ANDROID
)