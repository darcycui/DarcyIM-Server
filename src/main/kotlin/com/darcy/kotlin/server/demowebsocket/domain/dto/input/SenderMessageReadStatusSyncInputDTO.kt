package com.darcy.kotlin.server.demowebsocket.domain.dto.input

data class SenderMessageReadStatusSyncInputDTO(
    val userId: Long = 0L,
    val fromUserName : String = "",
    val targetId: Long = 0L,
    val targetName: String = "",
    val conversationType: Int = 1,
    val clientType: String = "",
    val deviceId: String = "",
    // 新增字段：用于标识离线期间的起始时间
    val since: String? = null,
    // 新增字段：用于标识离线期间的结束时间
    val until: String? = null
)
