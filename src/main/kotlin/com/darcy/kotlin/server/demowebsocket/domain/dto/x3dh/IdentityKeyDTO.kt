package com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.device.Device
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.IdentityKey
import java.time.LocalDateTime

/**
 * 身份密钥DTO - X3DH密钥交换协议
 */
data class IdentityKeyDTO(
    val id: Long = 0,
    val userId: Long = 0,
    val publicKey: String = "",
    val keySize: Int = 32,
    val keyFingerprint: String = "",
    val isCurrent: Boolean = true,
    val isRevoked: Boolean = false,
    val isCompromised: Boolean = false,
    val activatedAt: LocalDateTime? = null,
    val revokedAt: LocalDateTime? = null,
    val expiresAt: LocalDateTime? = null,
    val lastUsedAt: LocalDateTime? = null,
    val createdByIp: String? = null,
    val revokedByIp: String? = null,
    val revocationReason: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Entity 转 DTO（扩展方法）
 */
fun IdentityKey.toDTO(): IdentityKeyDTO {
    return IdentityKeyDTO(
        id = this.id,
        userId = this.user.id,
        publicKey = this.publicKey,
        keyFingerprint = this.keyFingerprint,
        isCurrent = this.isCurrent,
        isRevoked = this.isRevoked,
        isCompromised = this.isCompromised,
        activatedAt = this.activatedAt,
        revokedAt = this.revokedAt,
        expiresAt = this.expiresAt,
        lastUsedAt = this.lastUsedAt,
        createdByIp = this.createdByIp,
        revokedByIp = this.revokedByIp,
        revocationReason = this.revocationReason,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * DTO 转 Entity（扩展方法）
 * @param user 用户对象
 * @param device 设备对象
 */
fun IdentityKeyDTO.toEntity(user: User, device: Device): IdentityKey {
    return IdentityKey(
        user = user,
        publicKey = this.publicKey,
        keyFingerprint = this.keyFingerprint,
        isCurrent = this.isCurrent,
        isRevoked = this.isRevoked,
        isCompromised = this.isCompromised,
        activatedAt = this.activatedAt,
        revokedAt = this.revokedAt,
        expiresAt = this.expiresAt,
        lastUsedAt = this.lastUsedAt,
        createdByIp = this.createdByIp,
        revokedByIp = this.revokedByIp,
        revocationReason = this.revocationReason
    ).apply {
        if (this@toEntity.id != 0L) {
            this.id = this@toEntity.id
        }
    }
}

/**
 * Entity 列表转 DTO 列表（扩展方法）
 */
fun List<IdentityKey>.toDTOList(): List<IdentityKeyDTO> {
    return this.map { it.toDTO() }
}
