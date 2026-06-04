package com.darcy.kotlin.server.demowebsocket.domain.dto.input

import jakarta.validation.constraints.NotBlank

data class X3DHPushKeysRequestDTO(
    @field:NotBlank(message = "用户ID不能为空")
    var userId: Long = 0,

    @field:NotBlank(message = "身份公钥不能为空")
    val identityKey: String = "",

    @field:NotBlank(message = "预密钥不能为空")
    val signedPreKey: String = "",

    @field:NotBlank(message = "一次性预密钥不能为空")
    val oneTimePreKeys: List<OneTimePreKeyInputDTO> = listOf()
) : IRequestDTO

data class X3DHPullKeysRequestDTO(
    @field:NotBlank(message = "Alice用户ID不能为空")
    var aliceUserId: Long = 0,

    @field:NotBlank(message = "Bob用户ID不能为空")
    var bobUserId: Long = 0
) : IRequestDTO

data class X3DHPushHelloRequestDTO(
    @field:NotBlank(message = "Alice用户ID不能为空")
    var aliceUserId: Long = 0,

    @field:NotBlank(message = "Bob用户ID不能为空")
    var bobUserId: Long = 0,

    @field:NotBlank(message = "Alice的公钥不能为空")
    val aliceIdentityKey: String = "",

    @field:NotBlank(message = "Alice的临时公钥不能为空")
    val aliceEphemeralKey: String = "",

    @field:NotBlank(message = "Bob的预密钥索引不能为空")
    val bobOneTimePreKeyId: String = ""
) : IRequestDTO


data class X3DHPullHelloRequestDTO(
    @field:NotBlank(message = "Alice用户ID不能为空")
    var aliceUserId: Long = 0,

    @field:NotBlank(message = "Bob用户ID不能为空")
    var bobUserId: Long = 0,
) : IRequestDTO

data class OneTimePreKeyInputDTO(
    var id: Long = 0,
    var keyId: String = "",
    var userId: Long = 0,
    var publicKey: String = "",
) : IRequestDTO {
}