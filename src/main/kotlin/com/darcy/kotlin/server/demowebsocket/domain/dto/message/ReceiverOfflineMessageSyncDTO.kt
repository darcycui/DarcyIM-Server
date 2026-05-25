package com.darcy.kotlin.server.demowebsocket.domain.dto.message

import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil

/**
 * 接收方离线消息同步响应
 */
data class ReceiverOfflineMessageSyncDTO(
    val messages: List<PrivateMessageDTO> = emptyList(),  // 离线消息列表
    val hasMore: Boolean = false,                          // 是否还有更多消息
    val nextCursor: String? = null,                        // 下次拉取的游标（最后一条消息ID）
    val syncTime: String = TimeUtil.getCurrentTimeString(), // 服务端同步时间
    val unreadCount: Int = 0,                              // 未读消息总数
    val readStatusMap: Map<String, Boolean> = emptyMap(),  // 消息ID -> 已读状态映射
    val conflictMessages: List<String> = emptyList()       // 需要客户端处理冲突的消息ID列表
)