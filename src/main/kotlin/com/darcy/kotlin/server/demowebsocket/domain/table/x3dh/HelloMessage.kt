package com.darcy.kotlin.server.demowebsocket.domain.table.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.User
import jakarta.persistence.*

@Entity
@Table(
    name = "hello_messages",
    indexes = [
        Index(name = "idx_from_user_id", columnList = "from_user_id"),
        Index(name = "idx_to_user_id", columnList = "to_user_id"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_from_to_user", columnNames = ["from_user_id", "to_user_id"])
    ]
)
open class HelloMessage(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false, foreignKey = ForeignKey(name = "fk_hello_message_from_user"))
    open var fromUser: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false, foreignKey = ForeignKey(name = "fk_hello_message_to_user"))
    open var toUser: User,

    @Column(name = "alice_identity_key", nullable = false, length = 256)
    open var aliceIdentityKey: String = "",

    @Column(name = "alice_ephemeral_key", nullable = false, length = 256)
    open var aliceEphemeralKey: String = "",

    @Column(name = "bob_one_time_pre_key", nullable = false, length = 64)
    open var bobOneTimePreKeyId: String = "",
) : BaseEntity() {

}