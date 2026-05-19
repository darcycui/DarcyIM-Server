package com.darcy.kotlin.server.demowebsocket.domain.table.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.Device
import com.darcy.kotlin.server.demowebsocket.domain.table.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

/**
 * 一次性预密钥表 - X3DH密钥交换协议
 */
@Entity
@Table(
    name = "key_one_time_prekeys",
    indexes = [
        Index(name = "idx_user_device_unused", columnList = "user_id, is_used, is_expired"),
        Index(name = "idx_available", columnList = "user_id, is_used, is_expired, created_at"),
        Index(name = "idx_used_info", columnList = "used_by_user, used_at"),
        Index(name = "idx_expires", columnList = "expires_at"),
        Index(name = "idx_created", columnList = "created_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_device_key", columnNames = ["user_id", "key_id"])
    ]
)
@DynamicInsert
@DynamicUpdate
open class OneTimePreKey(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_onetime_prekey_user"))
    open var user: User,

    @Column(name = "key_id", nullable = false, length = 64)
    open var keyId: String,

    @Column(name = "public_key", nullable = false, length = 256)
    open var publicKey: String = "",

    @Column(name = "key_fingerprint", length = 64, insertable = false, updatable = false)
    open var keyFingerprint: String = "",

    @Column(name = "is_used")
    open var isUsed: Boolean = false,

    @Column(name = "is_expired")
    open var isExpired: Boolean = false,

    @Column(name = "expires_at")
    open var expiresAt: LocalDateTime? = null,

    @Column(name = "used_at")
    open var usedAt: LocalDateTime? = null,

    @Column(name = "used_by_user", length = 64)
    open var usedByUser: String? = null,

    @Column(name = "used_by_session_id", length = 128)
    open var usedBySessionId: String? = null
) : BaseEntity() {

    override fun toString(): String {
        return "KeyOneTimePrekeys(id=$id, userId=${user.id}, " +
                "isUsed=$isUsed, isExpired=$isExpired)"
    }
}
