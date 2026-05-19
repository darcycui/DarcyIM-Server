package com.darcy.kotlin.server.demowebsocket.domain.dto.input

data class OneTimePreKeyInputDTO(
    var id: Long = 0,
    var keyId: String = "",
    var userId: Long = 0,
    var publicKey: String = "",
) {
}