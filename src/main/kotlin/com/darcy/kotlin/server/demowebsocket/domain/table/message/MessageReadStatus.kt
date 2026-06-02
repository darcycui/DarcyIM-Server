package com.darcy.kotlin.server.demowebsocket.domain.table.message

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@Table(
    name = "message_read_status",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_msg_user", columnNames = ["msg_id", "user_id"])
    ],
    indexes = [
        Index(name = "idx_user_conversation", columnList = "user_id, conversation_type, target_id, read_time"),
        Index(name = "idx_msg_conversation", columnList = "msg_id, conversation_type, is_read"),
        Index(name = "idx_target_conversation", columnList = "target_id, conversation_type, read_time")
    ]
)
@DynamicInsert
@DynamicUpdate
open class MessageReadStatus(
    @Column(name = "msg_id", nullable = false, length = 64)
    open var msgId: String = "",

    // 多对一 多条消息状态对应一个用户
    // 外键 user_id 关联到用户表 外键名为fk_message_read_status_user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_message_read_status_user"))
    open var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false)
    open var conversationType: Conversation.ConversationType,

    @Column(name = "target_id", nullable = false)
    open var targetId: Long = 0L,

    @Column(name = "is_read", nullable = false)
    open var isRead: Boolean = false,

    @Column(name = "read_time")
    open var readTime: LocalDateTime? = null,

    @Column(name = "client_type", length = 20)
    open var clientType: String = "",

    @Column(name = "device_id", length = 64)
    open var deviceId: String = ""
) : BaseEntity() {
}