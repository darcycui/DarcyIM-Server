package com.darcy.kotlin.server.demowebsocket.domain.table.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

/**
 * 会话表 - Double Ratchet协议
 */
@Entity
@Table(
    name = "sessions",
    indexes = [
        Index(name = "idx_local_identity", columnList = "local_identity_key"),
        Index(name = "idx_remote_identity", columnList = "remote_identity_key")
    ]
)
@DynamicInsert
@DynamicUpdate
open class Session(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_from_id", nullable = false, foreignKey = ForeignKey(name = "fk_session_user_from"))
    open var userFrom: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_to_id", nullable = false, foreignKey = ForeignKey(name = "fk_session_user_to"))
    open var userTo: User,

    @Id
    @Column(name = "session_id", nullable = false, length = 128)
    open var sessionId: String = "",

    @Column(name = "local_identity_key", nullable = false, length = 256)
    open var ourIdentityKey: String = "",

    @Column(name = "remote_identity_key", nullable = false, length = 256)
    open var theirIdentityKey: String = "",

    @Column(name = "root_key", nullable = false, columnDefinition = "TEXT")
    open var rootKey: String = "",

    @Column(name = "sending_chain_key", columnDefinition = "TEXT")
    open var sendingChainKey: String? = null,

    @Column(name = "receiving_chain_key", columnDefinition = "TEXT")
    open var receivingChainKey: String? = null,

    @Column(name = "local_ratchet_key_private", columnDefinition = "TEXT")
    open var localRatchetKeyPrivate: String? = null,

    @Column(name = "remote_ratchet_key_public", columnDefinition = "TEXT")
    open var remoteRatchetKeyPublic: String? = null,

    @Column(name = "previous_counter")
    open var previousCounter: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "session_state", length = 20)
    open var sessionState: SessionState = SessionState.ACTIVE,
) : BaseEntity() {

    enum class SessionState {
        ACTIVE,
        STALE,
        CLOSED
    }

    override fun toString(): String {
        return "Session(id=$id, sessionId=$sessionId, local=${ourIdentityKey.take(20)}..., " +
                "remote=${theirIdentityKey.take(20)}..., state=$sessionState)"
    }
}
