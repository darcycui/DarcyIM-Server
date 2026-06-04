package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class DHExchangeRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    var userId: Long = 0,

    @field:NotBlank(message = "DH公钥不能为空")
    var publicKey: String = ""
) : IRequestDTO
