package com.darcy.kotlin.server.demowebsocket.domain.table.group

import com.darcy.kotlin.server.demowebsocket.domain.table.BaseEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "`groups`",
    indexes = [
        Index(name = "idx_group_id", columnList = "group_id", unique = true),
        Index(name = "idx_owner", columnList = "owner_id"),
        Index(name = "idx_status", columnList = "status, created_at")
    ]
)
@DynamicInsert
@DynamicUpdate
open class Group(

    @Column(name = "group_id", nullable = false, length = 32, unique = true)
    open var groupId: String = "",

    @Column(name = "group_name", nullable = false, length = 100)
    open var groupName: String = "",

    @Column(name = "avatar", length = 512)
    open var avatar: String = "",

    @Column(name = "description", length = 500)
    open var description: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    open var owner: User,

    @Column(name = "max_members", nullable = false)
    open var maxMembers: Int = 500,

    @Column(name = "current_members", nullable = false)
    open var currentMembers: Int = 1,

    @Enumerated(EnumType.STRING)
    @Column(name = "join_type", nullable = false)
    open var joinType: JoinType = JoinType.VERIFICATION,

    @Column(name = "join_question", length = 200)
    open var joinQuestion: String = "",

    @Column(name = "join_answer", length = 200)
    open var joinAnswer: String = "",

    @Lob
    @Column(name = "announcement")
    open var announcement: String = "",

    @Column(name = "announcement_publisher_id")
    open var announcementPublisherId: Long = 0L,

    @Column(name = "announcement_publish_time")
    open var announcementPublishTime: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: GroupStatus = GroupStatus.NORMAL,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions", columnDefinition = "json")
    open var permissions: Map<String, Boolean> = emptyMap(),
) : BaseEntity() {


    enum class GroupStatus(private val code: Int) {
        NORMAL(1),     // 正常
        DISSOLVED(2),  // 已解散
        BANNED(3)      // 被封禁
        ;

        companion object {

            fun fromCode(code: Int): GroupStatus {
                return GroupStatus.entries.find { it.code == code } ?: NORMAL
            }
        }

        fun toCode(): Int {
            return code
        }
    }

    enum class JoinType(val code: Int) {
        FREE(1),           // 自由加入
        VERIFICATION(2),   // 需要验证
        INVITATION(3)      // 仅限邀请

        ;

        companion object {

            fun fromCode(code: Int): JoinType {
                return entries.find { it.code == code } ?: FREE
            }
        }

        fun toCode(): Int {
            return code
        }
    }
}