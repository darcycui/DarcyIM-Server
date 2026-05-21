package com.darcy.kotlin.server.demowebsocket.domain.table

import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

/**
 * 设备表 - X3DH密钥交换协议
 */
@Entity
@Table(
    name = "devices",
    indexes = [
        Index(name = "idx_user_id", columnList = "user_id"),
        Index(name = "idx_device_name", columnList = "device_name"),
        Index(name = "idx_is_active", columnList = "is_active")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_device", columnNames = ["user_id", "device_name"])
    ]
)
@DynamicInsert
@DynamicUpdate
open class Device(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_device_user"))
    open var user: User,

    @Column(name = "device_name", length = 100)
    open var name: String? = null,

    @Column(name = "device_type", length = 50)
    open var deviceType: String? = null,

    @Column(name = "os_name", length = 50)
    open var osName: String? = null,

    @Column(name = "os_version", length = 50)
    open var osVersion: String? = null,

    @Column(name = "app_version", length = 50)
    open var appVersion: String? = null,

    @Column(name = "push_token", length = 256)
    open var pushToken: String? = null,

    @Column(name = "last_login_ip", length = 45)
    open var lastLoginIp: String? = null,

    @Column(name = "last_login_at")
    open var lastLoginAt: LocalDateTime? = null,

    @Column(name = "last_logout_at")
    open var lastLogoutAt: LocalDateTime? = null,

    @Column(name = "is_active")
    open var isActive: Boolean = true,

    @Column(name = "is_online")
    open var isOnline: Boolean = false
) : BaseEntity() {

    override fun toString(): String {
        return "Device(id=$id, name=$name, " +
                "isActive=$isActive, isOnline=$isOnline)"
    }
}