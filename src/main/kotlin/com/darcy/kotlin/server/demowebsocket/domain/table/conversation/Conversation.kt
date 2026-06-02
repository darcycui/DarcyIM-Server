package com.darcy.kotlin.server.demowebsocket.domain.table.conversation

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "conversations",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_conversation", columnNames = ["user_id", "conversation_type", "target_id"])
    ],
    indexes = [
        Index(name = "idx_user_last_time", columnList = "user_id, last_msg_time DESC"),
        Index(name = "idx_user_unread", columnList = "user_id, unread_count, is_muted"),
        Index(name = "idx_user_pinned", columnList = "user_id, is_pinned DESC, last_msg_time DESC")
    ]
)

@DynamicUpdate
open class Conversation(
    @Column(name = "conversation_id", nullable = false, length = 64, unique = true)
    open var conversationId: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_conversation_user"))
    open var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false)
    open var conversationType: ConversationType,

    @Column(name = "target_id", nullable = false)
    open var targetId: Long = 0L,

    @Column(name = "last_msg_id", length = 64)
    open var lastMsgId: String = "",

    @Column(name = "last_msg_content", length = 500)
    open var lastMsgContent: String = "",

    @Column(name = "last_msg_type")
    open var lastMsgType: Int = 0,

    @Column(name = "last_msg_sender_id")
    open var lastMsgSenderId: Long = 0L,

    @Column(name = "last_msg_time")
    open var lastMsgTime: LocalDateTime? = null,

    @Column(name = "unread_count", nullable = false)
    open var unreadCount: Int = 0,

    @Column(name = "is_muted", nullable = false)
    open var isMuted: Boolean = false,

    @Column(name = "is_pinned", nullable = false)
    open var isPinned: Boolean = false,

    @Column(name = "is_top", nullable = false)
    open var isTop: Boolean = false,

    @Column(name = "draft", length = 1000)
    open var draft: String = "",

    @Column(name = "draft_time")
    open var draftTime: LocalDateTime? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ext_data", columnDefinition = "json")
    open var extData: Map<String, Any> = emptyMap()
) : BaseEntity() {

    enum class ConversationType(val code: Int) {
        PRIVATE(1),  // 单聊
        GROUP(2);    // 群聊

        companion object {
            fun fromCode(code: Int): ConversationType {
                return entries.find { it.code == code } ?: PRIVATE
            }
        }

        fun toCode(): Int {
            return code
        }
    }
}