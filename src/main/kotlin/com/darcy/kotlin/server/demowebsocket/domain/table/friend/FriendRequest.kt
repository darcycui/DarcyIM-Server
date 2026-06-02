package com.darcy.kotlin.server.demowebsocket.domain.table.friend

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@Table(
    name = "friend_requests",
    indexes = [
        Index(name = "idx_from_user", columnList = "from_user_id, status, created_at"),
        Index(name = "idx_to_user", columnList = "to_user_id, status, created_at")
    ]
)
@DynamicInsert
@DynamicUpdate
open class FriendRequest(
    // 多对一 多个好友请求对应一个用户
    // 设置外键 from_user_id 关联到用户表 外键名为fk_friend_request_from_user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false, foreignKey = ForeignKey(name = "fk_friend_request_from_user"))
    open var fromUser: User,

    // 多对一 多个好友请求对应一个用户
    // 设置外键 to_user_id 关联到用户表 外键名为fk_friend_request_to_user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false, foreignKey = ForeignKey(name = "fk_friend_request_to_user"))
    open var toUser: User,

    @Column(name = "greeting", length = 200)
    open var greeting: String = "",

    @Column(name = "remark", length = 200)
    open var remark: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: RequestStatus = RequestStatus.PENDING,

    @Column(name = "handle_time")
    open var handleTime: LocalDateTime? = null,

    @Column(name = "handle_result", length = 50)
    open var handleResult: String = ""
) : BaseEntity() {

    enum class RequestStatus(val code: Int) {
        PENDING(1),    // 1-待处理
        ACCEPTED(2),   // 2-已接受
        REJECTED(3),   // 3-已拒绝
        IGNORED(4),    // 4-已忽略
        EXPIRED(5)     // 5-已过期
        ;

        companion object {
            fun fromCode(code: Int): RequestStatus {
                return entries.find { it.code == code } ?: PENDING
            }
        }

        fun toCode(): Int {
            return code
        }

        fun noNeedRequestFriendAgain(): Boolean {
            return when (this) {
                PENDING, ACCEPTED -> true
                REJECTED, IGNORED, EXPIRED -> false
                else -> false
            }
        }

    }
}