package com.darcy.kotlin.server.demowebsocket.domain.table.message

import com.alibaba.fastjson2.annotation.JSONField
import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

/**
 * 使用JPA注解创建数据库表
 */
@Entity
@Table(
    name = "private_messages",
    indexes = [
        Index(name = "idx_sender_receiver", columnList = "sender_id, receiver_id, send_time"),
        Index(name = "idx_receiver_sender", columnList = "receiver_id, sender_id, send_time"),
        Index(name = "idx_msg_id", columnList = "msg_id", unique = true),
        Index(name = "idx_send_time", columnList = "send_time"),
    ]
)
@DynamicInsert
open class PrivateMessage(

    @Column(name = "msg_id", nullable = false, length = 64, unique = true)
    open var msgId: String = "",

    // 多对一 多个私聊消息对应一个发送者
    // 设置sender_id是外键 关联到用户表 外键名为fk_private_message_sender
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false, foreignKey = ForeignKey(name = "fk_private_message_sender"))
    @JSONField(serialize = false)
    open var sender: User,

    // 多对一 多个私聊消息对应一个接收者
    // 设置receiver_id是外键 关联到用户表 外键名为fk_private_message_receiver
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false, foreignKey = ForeignKey(name = "fk_private_message_receiver"))
    @JSONField(serialize = false)
    open var receiver: User,

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

    @Column(name = "is_deleted_by_sender", nullable = false)
    open var isDeletedBySender: Boolean = false,

    @Column(name = "is_deleted_by_receiver", nullable = false)
    open var isDeletedByReceiver: Boolean = false,

    @Column(name = "seq_id", nullable = false)
    open var seqId: Long = 0L,

    @Column(name = "send_time", nullable = false)
    open var sendTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "read_time")
    open var readTime: LocalDateTime? = null,

    @Column(name = "recall_time")
    open var recallTime: LocalDateTime? = null,

    @Column(name = "reply_to_msg_id", length = 64)
    open var replyToMsgId: String = "",

    @Column(name = "client_msg_id", length = 64)
    open var clientMsgId: String = "",

    @Column(name = "client_type", length = 20)
    open var clientType: String = "",

    @Column(name = "client_version", length = 20)
    open var clientVersion: String = "",

    // 添加三个字段
    @Column(name = "dh_public_key", length = 128)
    open var dhPublicKey: String = "",

    @Column(name = "n_key")
    open var nKey: Long = 0L,

    @Column(name = "pn_key")
    open var pnKey: Long = 0L
) : BaseEntity() {
    // 序列化时 只保留 sender User 的 id
    @get:JSONField(name = "senderId")
    val senderId: Long
        get() = sender.id

    // 序列化时 只保留 receiver User 的 id
    @get:JSONField(name = "receiverId")
    val receiverId: Long
        get() = receiver.id

    enum class MessageType(private val value: Int) {
        TEXT(1),           // 文本
        IMAGE(2),          // 图片
        VOICE(3),          // 语音
        VIDEO(4),          // 视频
        FILE(5),           // 文件
        LOCATION(6),       // 位置
        CARD(7),           // 名片
        EMOJI(8),          // 表情
        SYSTEM(9),         // 系统消息
        CUSTOM(10)          // 自定义消息
        ;

        companion object {
            fun fromValue(value: Int): MessageType {
                return entries.first { it.value == value }
            }
        }

        fun toValue(): Int {
            return value
        }
    }
}