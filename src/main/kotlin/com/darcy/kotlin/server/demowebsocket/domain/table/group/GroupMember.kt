package com.darcy.kotlin.server.demowebsocket.domain.table.group

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User

import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@Table(
    name = "group_members",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_group_user", columnNames = ["group_id", "user_id"])
    ],
    indexes = [
        Index(name = "idx_user_group", columnList = "user_id, group_id"),
        Index(name = "idx_role", columnList = "role, join_time"),
        Index(name = "idx_group_role", columnList = "group_id, role, join_time")
    ]
)
@DynamicInsert
@DynamicUpdate
open class GroupMember(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    open var group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User,

    @Column(name = "alias", length = 64)
    open var alias: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    open var role: MemberRole = MemberRole.MEMBER,

    @Column(name = "join_time", nullable = false, updatable = false)
    open var joinTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_read_msg_id")
    open var lastReadMsgId: Long = 0,

    @Column(name = "unread_count", nullable = false)
    open var unreadCount: Int = 0,

    @Column(name = "is_muted", nullable = false)
    open var isMuted: Boolean = false,

    @Column(name = "mute_until")
    open var muteUntil: LocalDateTime? = null,

    @Column(name = "is_pinned", nullable = false)
    open var isPinned: Boolean = false,

    @Column(name = "is_show_nickname", nullable = false)
    open var isShowNickname: Boolean = true,

    @Column(name = "inviter_id")
    open var inviterId: Long = 0L,

    @Column(name = "invite_time")
    open var inviteTime: LocalDateTime? = null
) : BaseEntity() {

    enum class MemberRole(val code: Int) {
        OWNER(1),      // 群主
        ADMIN(2),      // 管理员
        MEMBER(3)      // 普通成员
        ;

        companion object {
            fun fromCode(code: Int): MemberRole {
                return entries.firstOrNull { it.code == code } ?: MEMBER
            }
        }

        fun toCode(): Int {
            return code
        }
    }
}