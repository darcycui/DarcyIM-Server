package com.darcy.kotlin.server.demowebsocket.domain.dto.input

data class ReceiverMessageReadStatusMarkInputDTO(
    val userId: Long = 0L,
    val fromUserName : String = "",
    val targetId: Long = 0L,
    val targetName: String = "",
    val msgIds: List<String> = listOf(),
    val conversationId: Long = 1,
    val conversationType: Int = 1,
    val clientType: String = "",
    val deviceId: String = ""
)
