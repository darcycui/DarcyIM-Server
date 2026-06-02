package com.darcy.kotlin.server.demowebsocket.domain.table.group

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@Table(
    name = "group_invites",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_group_invite", columnNames = ["group_id", "invitee_id", "status"])
    ],
    indexes = [
        Index(name = "idx_group_status", columnList = "group_id, status, created_at"),
        Index(name = "idx_invitee_status", columnList = "invitee_id, status, created_at"),
        Index(name = "idx_inviter", columnList = "inviter_id, created_at"),
        Index(name = "idx_expire_time", columnList = "expire_time, status")
    ]
)
@DynamicInsert
@DynamicUpdate
open class GroupInvite(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = ForeignKey(name = "fk_group_invite_group"))
    open var group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false, foreignKey = ForeignKey(name = "fk_group_invite_inviter"))
    open var inviter: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false, foreignKey = ForeignKey(name = "fk_group_invite_invitee"))
    open var invitee: User,

    @Column(name = "message", length = 200)
    open var message: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: InviteStatus = InviteStatus.PENDING,

    @Column(name = "expire_time", nullable = false)
    open var expireTime: LocalDateTime = LocalDateTime.now().plusDays(7),

    @Column(name = "handle_time")
    open var handleTime: LocalDateTime? = null,

    @Column(name = "handle_remark", length = 200)
    open var handleRemark: String = "",

    @Column(name = "is_reminded", nullable = false)
    open var isReminded: Boolean = false,

    @Column(name = "remind_count", nullable = false)
    open var remindCount: Int = 0
) : BaseEntity() {

    enum class InviteStatus(val code: Int) {
        PENDING(1),    // 待处理
        ACCEPTED(2),   // 已接受
        REJECTED(3),   // 已拒绝
        EXPIRED(4),    // 已过期
        CANCELLED(5)   // 已取消（邀请人撤回）
        ;

        companion object {
            fun fromCode(code: Int): InviteStatus {
                return entries.firstOrNull { it.code == code } ?: PENDING
            }
        }

        fun toCode(): Int {
            return code
        }
    }
}
