package com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.Device
import com.darcy.kotlin.server.demowebsocket.domain.table.User
import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.OneTimePreKey
import java.time.LocalDateTime

/**
 * 一次性预密钥DTO - X3DH密钥交换协议
 */
data class OneTimePreKeyDTO(
    val id: Long = 0,
    val keyId: String = "",
    val userId: Long = 0,
    val publicKey: String = "",
    val isUsed: Boolean = false,
    val isExpired: Boolean = false,
    val expiresAt: LocalDateTime? = null,
    val usedAt: LocalDateTime? = null,
    val usedByUser: String? = null,
    val usedBySessionId: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Entity 转 DTO（扩展方法）
 */
fun OneTimePreKey.toDTO(): OneTimePreKeyDTO {
    return OneTimePreKeyDTO(
        id = this.id,
        userId = this.user.id,
        publicKey = this.publicKey,
        isUsed = this.isUsed,
        isExpired = this.isExpired,
        expiresAt = this.expiresAt,
        usedAt = this.usedAt,
        usedByUser = this.usedByUser,
        usedBySessionId = this.usedBySessionId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * DTO 转 Entity（扩展方法）
 * @param user 用户对象
 * @param device 设备对象
 */
fun OneTimePreKeyDTO.toEntity(user: User, device: Device): OneTimePreKey {
    return OneTimePreKey(
        keyId = this.keyId,
        user = user,
        publicKey = this.publicKey,
        isUsed = this.isUsed,
        isExpired = this.isExpired,
        expiresAt = this.expiresAt,
        usedAt = this.usedAt,
        usedByUser = this.usedByUser,
        usedBySessionId = this.usedBySessionId
    ).apply {
        if (this@toEntity.id != 0L) {
            this.id = this@toEntity.id
        }
    }
}

/**
 * Entity 列表转 DTO 列表（扩展方法）
 */
fun List<OneTimePreKey>.toDTOList(): List<OneTimePreKeyDTO> {
    return this.map { it.toDTO() }
}
