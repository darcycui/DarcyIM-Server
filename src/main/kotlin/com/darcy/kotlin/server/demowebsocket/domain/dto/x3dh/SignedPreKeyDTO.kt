package com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.device.Device
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.SignedPreKey
import java.time.LocalDateTime

/**
 * 已签名的预密钥DTO - X3DH密钥交换协议
 */
data class SignedPreKeyDTO(
    val id: Long = 0,
    val userId: Long = 0,
    val publicKey: String = "",
    val signature: String = "",
    val isCurrent: Boolean = true,
    val isExpired: Boolean = false,
    val isRevoked: Boolean = false,
    val expiresAt: LocalDateTime? = null,
    val usedAt: LocalDateTime? = null,
    val useCount: Int = 0,
    val lastUsedByUser: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Entity 转 DTO（扩展方法）
 */
fun SignedPreKey.toDTO(): SignedPreKeyDTO {
    return SignedPreKeyDTO(
        id = this.id,
        userId = this.user.id,
        publicKey = this.publicKey,
        signature = this.signature,
        isCurrent = this.isCurrent,
        isExpired = this.isExpired,
        isRevoked = this.isRevoked,
        expiresAt = this.expiresAt,
        usedAt = this.usedAt,
        useCount = this.useCount,
        lastUsedByUser = this.lastUsedByUser,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * DTO 转 Entity（扩展方法）
 * @param user 用户对象
 * @param device 设备对象
 */
fun SignedPreKeyDTO.toEntity(user: User, device: Device): SignedPreKey {
    return SignedPreKey(
        user = user,
        publicKey = this.publicKey,
        signature = this.signature,
        isCurrent = this.isCurrent,
        isExpired = this.isExpired,
        isRevoked = this.isRevoked,
        expiresAt = this.expiresAt,
        usedAt = this.usedAt,
        useCount = this.useCount,
        lastUsedByUser = this.lastUsedByUser
    )
}

/**
 * Entity 列表转 DTO 列表（扩展方法）
 */
fun List<SignedPreKey>.toDTOList(): List<SignedPreKeyDTO> {
    return this.map { it.toDTO() }
}
