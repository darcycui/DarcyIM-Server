package com.darcy.kotlin.server.demowebsocket.domain.table.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

/**
 * 已签名的预密钥表 - X3DH密钥交换协议
 */
@Entity
@Table(
    name = "key_signed_prekeys",
    indexes = [
        Index(name = "idx_user_device", columnList = "user_id"),
        Index(name = "idx_expired", columnList = "is_expired, expires_at"),
        Index(name = "idx_current", columnList = "is_current"),
        Index(name = "idx_created", columnList = "created_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_device_key", columnNames = ["user_id", "key_id"]),
        UniqueConstraint(name = "uk_current_per_device", columnNames = ["user_id", "is_current"])
    ]
)
@DynamicInsert
@DynamicUpdate
open class SignedPreKey(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_signed_prekey_user"))
    open var user: User,

    @Column(name = "key_fingerprint", length = 64, insertable = false, updatable = false)
    open var keyFingerprint: String = "",

    @Column(name = "public_key", nullable = false, length = 256)
    open var publicKey: String = "",

    @Column(name = "signature", nullable = false, length = 256)
    open var signature: String = "",

    @Column(name = "is_current")
    open var isCurrent: Boolean = true,

    @Column(name = "is_expired")
    open var isExpired: Boolean = false,

    @Column(name = "is_revoked")
    open var isRevoked: Boolean = false,

    @Column(name = "expires_at")
    open var expiresAt: LocalDateTime? = null,

    @Column(name = "used_at")
    open var usedAt: LocalDateTime? = null,

    @Column(name = "use_count")
    open var useCount: Int = 0,

    @Column(name = "last_used_by_user", length = 64)
    open var lastUsedByUser: String? = null
) : BaseEntity() {

    override fun toString(): String {
        return "KeySignedPrekeys(id=$id, userId=${user.id}, " +
                "isCurrent=$isCurrent, isExpired=$isExpired, useCount=$useCount)"
    }
}
