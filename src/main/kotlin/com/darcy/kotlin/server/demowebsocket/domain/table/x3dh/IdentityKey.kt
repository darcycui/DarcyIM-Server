package com.darcy.kotlin.server.demowebsocket.domain.table.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

/**
 * 身份密钥表 - X3DH密钥交换协议
 */
@Entity
@Table(
    name = "key_identity_keys",
    indexes = [
        Index(name = "idx_user_device", columnList = "user_id"),
        Index(name = "idx_fingerprint", columnList = "key_fingerprint"),
        Index(name = "idx_created", columnList = "created_at"),
        Index(name = "idx_expires", columnList = "expires_at"),
        Index(name = "idx_active_revoked", columnList = "is_current, is_revoked, is_compromised")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_public_key", columnNames = ["public_key"]),
        UniqueConstraint(name = "uk_current_per_device", columnNames = ["user_id", "is_current"])
    ]
)
@DynamicInsert
@DynamicUpdate
open class IdentityKey(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_key_identity_user"))
    open var user: User,

    @Column(name = "public_key", nullable = false, length = 256)
    open var publicKey: String = "",

    @Column(name = "key_fingerprint", length = 64, insertable = false, updatable = false)
    open var keyFingerprint: String = "",

    @Column(name = "is_current")
    open var isCurrent: Boolean = true,

    @Column(name = "is_revoked")
    open var isRevoked: Boolean = false,

    @Column(name = "is_compromised")
    open var isCompromised: Boolean = false,

    @Column(name = "activated_at")
    open var activatedAt: LocalDateTime? = null,

    @Column(name = "revoked_at")
    open var revokedAt: LocalDateTime? = null,

    @Column(name = "expires_at")
    open var expiresAt: LocalDateTime? = null,

    @Column(name = "last_used_at")
    open var lastUsedAt: LocalDateTime? = null,

    @Column(name = "created_by_ip", length = 45)
    open var createdByIp: String? = null,

    @Column(name = "revoked_by_ip", length = 45)
    open var revokedByIp: String? = null,

    @Column(name = "revocation_reason", length = 100)
    open var revocationReason: String? = null
) : BaseEntity() {

    override fun toString(): String {
        return "KeyIdentity(id=$id, userId=${user.id}, " +
                "isCurrent=$isCurrent, isRevoked=$isRevoked, keyFingerprint=$keyFingerprint)"
    }
}
