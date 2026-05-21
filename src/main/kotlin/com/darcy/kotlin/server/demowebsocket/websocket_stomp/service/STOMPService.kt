package com.darcy.kotlin.server.demowebsocket.websocket_stomp.service

import com.darcy.kotlin.server.demowebsocket.domain.dto.message.GroupMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toEntity
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.exception.code800.STOMPException
import com.darcy.kotlin.server.demowebsocket.http.service.*
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class STOMPService @Autowired constructor(
    private val websocket: SimpMessagingTemplate,
    private val privateMessageService: PrivateMessageService,
    private val groupMessageService: GroupMessageService,
    private val groupService: GroupService,
    private val userService: UserService,
    private val messageReadStatusService: MessageReadStatusService
//    @Lazy
//    private val simpUserRegistry: SimpUserRegistry
) {
    fun sendPrivate(
        privateMessage: PrivateMessageDTO,
        fromUserId: String,
        dhPublicKey: String,
        sendingIndex: Long,
        receivingIndex: Long
    ) {
        val recipient = privateMessage.receiverName
        kotlin.runCatching {
            val headers =
                mapOf(
                    "fromUserId" to fromUserId,
                    "dhPublicKey" to dhPublicKey,
                    "sendingIndex" to sendingIndex,
                    "receivingIndex" to receivingIndex,
                )
            val sendUser = userService.queryUserById(privateMessage.senderId)
            val receiveUser = userService.queryUserById(privateMessage.receiverId)
            val savedMessage = privateMessageService.createMessage(privateMessage.toEntity(sendUser, receiveUser))
            DarcyLogger.info("保存消息: msgId=${savedMessage.msgId} receiverId=${privateMessage.receiverId}")
            messageReadStatusService.createOrUpdateReadStatus(
                msgId = savedMessage.msgId,
                userId = privateMessage.receiverId,
                conversationType = Conversation.ConversationType.PRIVATE,
                targetId = privateMessage.senderId,
                isRead = false
            )
            DarcyLogger.info("创建消息已读状态: msgId=${savedMessage.msgId}, receiverId=${privateMessage.receiverId}")
            DarcyLogger.warn("单发消息 -->$recipient headers=$headers message=$privateMessage")
            // Spring STOMP 单播 Unicast
            websocket.convertAndSendToUser(
                recipient,
                "/queue/message",
                privateMessage.copy(msgId = savedMessage.msgId),
                headers
            )

        }.onSuccess {
            DarcyLogger.info("send private message SUCCESS")
        }.onFailure {
            DarcyLogger.error("send private message FAILED: ${it::class.java.simpleName} ${it.message}")
            when (it) {
                is IllegalArgumentException -> {
                    if (it.message?.contains("Cannot send a message when session is closed") == true) {
                        DarcyLogger.warn("用户已下线 无法发送消息 这里记录状态到数据库，忽略异常")
                    } else {
                        it.printStackTrace()
                    }
                }

                else -> {
                    it.printStackTrace()
                }
            }
            throw STOMPException.STOMP_SEND_PRIVATE_MESSAGE_FAILED
        }
    }

//    fun isUserOnline(username: String): Boolean {
//        return simpUserRegistry.getUser(username)?.hasSessions() ?: false
//    }

    fun sendAllGroup(groupMessage: GroupMessageDTO) {
        kotlin.runCatching {
            DarcyLogger.warn("群发消息All -->/topic/message $groupMessage")
            // Spring STOMP 广播 Broadcast - 广播给所有订阅者
            websocket.convertAndSend("/topic/message", groupMessage)
            val sender = userService.queryUserById(groupMessage.senderId)
            val group = groupService.queryGroupById(groupMessage.groupId)
            val savedMessage = groupMessageService.createMessage(groupMessage.toEntity(sender, group))
            val members = groupService.queryAllGroupMembersById(groupMessage.groupId)
            members.forEach { member ->
                if (member.id != groupMessage.senderId) {
                    messageReadStatusService.createOrUpdateReadStatus(
                        msgId = savedMessage.msgId,
                        userId = member.id,
                        conversationType = Conversation.ConversationType.GROUP,
                        targetId = groupMessage.groupId,
                        isRead = false
                    )
                }
            }

            DarcyLogger.info("创建群消息已读状态: msgId=${savedMessage.msgId}, memberCount=${members.size}")

        }.onSuccess {
            DarcyLogger.info("send all group message SUCCESS")
        }.onFailure {
            DarcyLogger.error("send all group message FAILED")
            it.printStackTrace()
            throw STOMPException.STOMP_SEND_ALL_GROUP_MESSAGE_FAILED
        }
    }

    fun sendTargetGroup(groupMessage: GroupMessageDTO) {
        kotlin.runCatching {
            val groupId = groupMessage.groupId
            DarcyLogger.warn("群发消息 -->/topic/group/$groupId $groupMessage")
            // Spring STOMP 广播 Broadcast - 只发送给指定群组的订阅者
            websocket.convertAndSend("/topic/group/$groupId", groupMessage)
            val sender = userService.queryUserById(groupMessage.senderId)
            val group = groupService.queryGroupById(groupMessage.groupId)
            val savedMessage = groupMessageService.createMessage(groupMessage.toEntity(sender, group))
            val members = groupService.queryAllGroupMembersById(groupId)
            members.forEach { member ->
                if (member.id != groupMessage.senderId) {
                    messageReadStatusService.createOrUpdateReadStatus(
                        msgId = savedMessage.msgId,
                        userId = member.id,
                        conversationType = Conversation.ConversationType.GROUP,
                        targetId = groupId,
                        isRead = false
                    )
                }
            }
        }.onSuccess {
            DarcyLogger.info("send target group message SUCCESS")
        }.onFailure {
            DarcyLogger.error("send target group message FAILED")
            it.printStackTrace()
            throw STOMPException.STOMP_SEND_TARGET_GROUP_MESSAGE_FAILED
        }
    }
}