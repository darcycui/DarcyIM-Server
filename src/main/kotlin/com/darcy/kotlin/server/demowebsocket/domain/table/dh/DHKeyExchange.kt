package com.darcy.kotlin.server.demowebsocket.domain.table.dh

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

/**
 * DH密钥交换表 - 用于与客户端进行DH密钥交换
 */
@Entity
@Table(
    name = "dh_key_exchanges",
    indexes = [
        Index(name = "idx_user_session", columnList = "user_id, session_id"),
        Index(name = "idx_public_key", columnList = "public_key"),
        Index(name = "idx_created_at", columnList = "created_at"),
        Index(name = "idx_expires_at", columnList = "expires_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_session_id", columnNames = ["session_id"])
    ]
)
@DynamicInsert
@DynamicUpdate
open class DHKeyExchange(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_dh_exchange_user"))
    open var user: User,

    @Column(name = "session_id", nullable = false, length = 64, unique = true)
    open var sessionId: String = "",

    @Column(name = "remote_public_key", nullable = false, length = 256)
    open var remotePublicKey: String = "",

    @Column(name = "public_key", nullable = false, length = 256)
    open var publicKey: String = "",

    @Column(name = "private_key", nullable = false, length = 256)
    open var privateKey: String = "",

    @Column(name = "key_size", nullable = false)
    open var keySize: Int = 2048,

    @Column(name = "algorithm", length = 32)
    open var algorithm: String = "",

    @Column(name = "shared_secret", length = 256)
    open var sharedSecret: String? = null,

    @Column(name = "is_completed")
    open var isCompleted: Boolean = false,

    @Column(name = "is_expired")
    open var isExpired: Boolean = false,

    @Column(name = "expires_at")
    open var expiresAt: LocalDateTime? = null,

    @Column(name = "completed_at")
    open var completedAt: LocalDateTime? = null,

    @Column(name = "client_ip", length = 45)
    open var clientIp: String? = null,

    @Column(name = "user_agent", length = 512)
    open var userAgent: String? = null
) : BaseEntity() {

    override fun toString(): String {
        return "DHKeyExchange(id=$id, userId=${user.id}, sessionId=$sessionId, " +
                "isCompleted=$isCompleted, isExpired=$isExpired, algorithm=$algorithm)"
    }
}
