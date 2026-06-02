package com.darcy.kotlin.server.demowebsocket.domain.table.message

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import com.darcy.kotlin.server.demowebsocket.domain.table.group.Group
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "group_messages",
    indexes = [
        Index(name = "idx_group_send_time", columnList = "group_id, send_time"),
        Index(name = "idx_sender_group", columnList = "sender_id, group_id, send_time"),
        Index(name = "idx_msg_id", columnList = "msg_id", unique = true),
    ]
)
@DynamicInsert
open class GroupMessage(

    @Column(name = "msg_id", nullable = false, length = 64, unique = true)
    open var msgId: String = "",

    // 多对一 多条群消息对应一个群组
    // 外键 group_id 关联到群组表 外键名为fk_group_message_group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = ForeignKey(name = "fk_group_message_group"))
    open var group: Group,

    // 多对一 多条群消息对应一个发送者
    // 外键 sender_id 关联到用户表 外键名为fk_group_message_sender
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false, foreignKey = ForeignKey(name = "fk_group_message_sender"))
    open var sender: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "msg_type", nullable = false)
    open var msgType: MessageType = MessageType.TEXT,

    @Lob
    @Column(name = "content")
    open var content: String = "",

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_data", columnDefinition = "json")
    open var extraData: Map<String, Any> = emptyMap(),

    @Column(name = "is_recalled", nullable = false)
    open var isRecalled: Boolean = false,

    @Column(name = "seq_id", nullable = false)
    open var seqId: Long = 0,

    @Column(name = "send_time", nullable = false)
    open var sendTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "recall_time")
    open var recallTime: LocalDateTime? = null,

    @Column(name = "is_at_all", nullable = false)
    open var isAtAll: Boolean = false,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "at_users", columnDefinition = "json")
    open var atUsers: List<Long> = emptyList(),

    @Column(name = "reply_to_msg_id", length = 64)
    open var replyToMsgId: String = "",

    @Column(name = "read_count", nullable = false)
    open var readCount: Int = 0,

    @Column(name = "total_members", nullable = false)
    open var totalMembers: Int = 1
) : BaseEntity() {

    enum class MessageType(private val code: Int) {
        TEXT(1),
        IMAGE(2),
        VOICE(3),
        VIDEO(4),
        FILE(5),
        LOCATION(6),
        CARD(7),
        EMOJI(8),
        SYSTEM(9),
        CUSTOM(10)
        ;

        companion object {
            fun fromCode(code: Int): MessageType {
                return entries.find { it.code == code } ?: TEXT
            }
        }

        fun toCode(): Int {
            return code
        }
    }
}