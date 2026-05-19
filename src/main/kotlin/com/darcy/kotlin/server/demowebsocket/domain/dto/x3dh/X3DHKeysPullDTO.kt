package com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh

data class X3DHKeysPullDTO(
    val identityKey: String = "",
    val signedPreKey: String = "",
    val oneTimePreKey: String = "",
    val oneTimePreKeyId: String = ""
)