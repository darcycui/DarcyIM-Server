package com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh

data class X3DHPushKeysDTO(
    val userId: Long = 0,
    val status: Int = 0,
    val message: String = "",
)