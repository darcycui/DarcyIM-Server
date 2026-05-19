package com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh

import com.darcy.kotlin.server.demowebsocket.domain.table.User
import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.HelloMessage
import org.springframework.data.domain.Page
import java.time.LocalDateTime

/**
 * 问候消息DTO - X3DH密钥交换协议
 */
data class HelloMessageDTO(
    val id: Long = 0,
    val fromUserId: Long = 0,
    val toUserId: Long = 0,
    val aliceIdentityKey: String = "",
    val aliceEphemeralKey: String = "",
    val bobOneTimePreKeyId: String = "",
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Entity 转 DTO（扩展方法）
 */
fun HelloMessage.toDTO(): HelloMessageDTO {
    return HelloMessageDTO(
        id = this.id,
        fromUserId = this.fromUser.id,
        toUserId = this.toUser.id,
        aliceIdentityKey = this.aliceIdentityKey,
        aliceEphemeralKey = this.aliceEphemeralKey,
        bobOneTimePreKeyId = this.bobOneTimePreKeyId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * DTO 转 Entity（扩展方法）
 * @param fromUser 发送方用户对象
 * @param toUser 接收方用户对象
 */
fun HelloMessageDTO.toEntity(fromUser: User, toUser: User): HelloMessage {
    return HelloMessage(
        fromUser = fromUser,
        toUser = toUser,
        aliceIdentityKey = this.aliceIdentityKey,
        aliceEphemeralKey = this.aliceEphemeralKey,
        bobOneTimePreKeyId = this.bobOneTimePreKeyId

    )
}

/**
 * Entity 列表转 DTO 列表（扩展方法）
 */
fun List<HelloMessage>.toDTOList(): List<HelloMessageDTO> {
    return this.map { it.toDTO() }
}

/**
 * Entity 列表转 DTO 列表（扩展方法）
 */
fun Page<HelloMessage>.toDTOList(): Page<HelloMessageDTO> {
    return this.map { it.toDTO() }
}
