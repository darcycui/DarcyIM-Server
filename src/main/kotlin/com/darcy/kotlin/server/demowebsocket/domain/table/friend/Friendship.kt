package com.darcy.kotlin.server.demowebsocket.domain.table.friend

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@Table(
    name = "friend_relationships",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_friend", columnNames = ["user_id", "friend_id"])
    ],
    indexes = [
        Index(name = "idx_friend_user", columnList = "friend_id, user_id"),
        Index(name = "idx_relation_status", columnList = "relation_status, created_at")
    ]
)
@DynamicInsert
@DynamicUpdate
open class Friendship(
    // 多对一 多个好友关系对应一个用户
    // 设置user_id是外键 关联到用户表 外键名为fk_friend_user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_friend_user"))
    open var user: User,

    // 多对一 多个好友关系对应一个用户
    // 设置friend_id是外键 关联到用户表 外键名为fk_friend_friend
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false, foreignKey = ForeignKey(name = "fk_friend_friend"))
    open var friend: User,

    @Column(name = "alias", length = 64)
    open var alias: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_status", nullable = false)
    open var relationStatus: RelationStatus = RelationStatus.PENDING,

    @Column(name = "source", length = 20)
    open var source: String = "",

    @Column(name = "greeting", length = 200)
    open var greeting: String = "",

    @Column(name = "added_at")
    open var addedAt: LocalDateTime? = null,

    @Column(name = "is_blocked", nullable = false)
    open var isBlocked: Boolean = false,

    @Column(name = "is_muted", nullable = false)
    open var isMuted: Boolean = false,

    @Column(name = "is_pinned", nullable = false)
    open var isPinned: Boolean = false,
) : BaseEntity() {

    enum class RelationStatus(val code: Int) {
        PENDING(1),    // 待处理
        FRIEND(2),     // 已是好友
        REJECTED(3),   // 已拒绝
        DELETED(4)     // 已删除
        ;

        companion object {
            fun fromCode(code: Int): RelationStatus {
                return entries.first { it.code == code }
            }
        }

        fun toCode(): Int {
            return code
        }
    }

    @PrePersist
    @PreUpdate
    fun prePersist() {
        if (relationStatus == RelationStatus.FRIEND && addedAt == null) {
            addedAt = LocalDateTime.now()
        }
    }
}