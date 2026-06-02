package com.darcy.kotlin.server.demowebsocket.domain.dto

import com.darcy.kotlin.server.demowebsocket.domain.table.dh.DHKeyExchange
import java.time.LocalDateTime

/**
 * DH密钥交换DTO - 用于与客户端进行DH密钥交换
 */
data class DHKeyExchangeDTO(
    val id: Long = 0,
    val userId: Long = 0,
    val sessionId: String = "",
    val publicKey: String = "",
    val keySize: Int = 2048,
    val algorithm: String = "",
    val isCompleted: Boolean = false,
    val isExpired: Boolean = false,
    val expiresAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)

/**
 * Entity 转 DTO（扩展方法）
 */
fun DHKeyExchange.toDTO(): DHKeyExchangeDTO {
    return DHKeyExchangeDTO(
        id = this.id,
        userId = this.user.id,
        sessionId = this.sessionId,
        publicKey = this.publicKey,
        keySize = this.keySize,
        algorithm = this.algorithm,
        isCompleted = this.isCompleted,
        isExpired = this.isExpired,
        expiresAt = this.expiresAt,
        completedAt = this.completedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * DTO 转 Entity（扩展方法）
 * @param userId 用户ID
 */
fun DHKeyExchangeDTO.toEntity(userId: Long): DHKeyExchange {
    val dummyUser = com.darcy.kotlin.server.demowebsocket.domain.table.user.User().apply {
        this.id = userId
    }

    return DHKeyExchange(
        user = dummyUser,
        sessionId = this.sessionId,
        publicKey = this.publicKey,
        privateKey = "", // 实际使用时需要设置真实的私钥
        keySize = this.keySize,
        algorithm = this.algorithm
    ).apply {
        this.id = this@toEntity.id
        this.isCompleted = this@toEntity.isCompleted
        this.isExpired = this@toEntity.isExpired
        this.expiresAt = this@toEntity.expiresAt
        this.completedAt = this@toEntity.completedAt
    }
}

/**
 * List<Entity> 转 List<DTO>（扩展方法）
 */
fun List<DHKeyExchange>.toDTO(): List<DHKeyExchangeDTO> {
    return this.map { it.toDTO() }
}
