package com.darcy.kotlin.server.demowebsocket.websocket_stomp.service

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkRequestDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.*
import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.exception.code800.STOMPException
import com.darcy.kotlin.server.demowebsocket.http.service.*
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.JsonUtil
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SEND_ALL_GROUP_MESSAGE_URL
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SEND_PRIVATE_MESSAGE_READ_URL
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SEND_PRIVATE_MESSAGE_URL
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SEND_TARGET_GROUP_MESSAGE_URL_PREFIX
import org.hibernate.dialect.JsonHelper
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
        fromUserId: Long,
        toUserId: Long,
        dhPublicKey: String,
        N: Long,
        PN: Long,
        url: String
    ) {
        kotlin.runCatching {
            val recipient = privateMessage.receiverName
            val headers = mapOf(
                "fromUserId" to fromUserId,
                "toUserId" to privateMessage.receiverId,
                "dhPublicKey" to dhPublicKey,
                "N_KEY" to N,
                "PN_KEY" to PN,
                "url" to url
            )
            val sendUser = userService.queryUserById(privateMessage.senderId)
            val receiveUser = userService.queryUserById(privateMessage.receiverId)
            val savedMessage = privateMessageService.createMessage(
                privateMessage.toEntity(sendUser, receiveUser, dhPublicKey, N, PN)
            )
            DarcyLogger.info("保存消息: msgId=${savedMessage.msgId} receiverId=${privateMessage.receiverId}")
            messageReadStatusService.senderCreateOrUpdateReadStatus(
                msgId = savedMessage.msgId,
                userId = privateMessage.senderId,
                targetId = privateMessage.receiverId,
                conversationType = Conversation.ConversationType.PRIVATE,
                isRead = false
            )
            DarcyLogger.info("创建消息已读状态: msgId=${savedMessage.msgId}, receiverId=${privateMessage.receiverId}")
            DarcyLogger.warn("单聊消息 -->$recipient headers=$headers message=$privateMessage")
            // Spring STOMP 单播 Unicast
            websocket.convertAndSendToUser(
                recipient,
                SEND_PRIVATE_MESSAGE_URL,
                privateMessage,
                headers
            )
        }.onSuccess {
            DarcyLogger.info("发送单聊消息成功")
        }.onFailure {
            DarcyLogger.error("发送单聊消息失败: ${it::class.java.simpleName} ${it.message}")
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
            DarcyLogger.warn("所有人消息 --> $groupMessage")
            // Spring STOMP 广播 Broadcast - 广播给所有订阅者
            websocket.convertAndSend(SEND_ALL_GROUP_MESSAGE_URL, groupMessage)
            val sender = userService.queryUserById(groupMessage.senderId)
            val group = groupService.queryGroupById(groupMessage.groupId)
            val savedMessage = groupMessageService.createMessage(groupMessage.toEntity(sender, group))
            val members = groupService.queryAllGroupMembersById(groupMessage.groupId)
            members.forEach { member ->
                if (member.id != groupMessage.senderId) {
                    messageReadStatusService.senderCreateOrUpdateReadStatus(
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
            DarcyLogger.info("发送所有人消息成功")
        }.onFailure {
            DarcyLogger.error("发送所有人消息失败: ${it::class.java.simpleName} ${it.message}")
            it.printStackTrace()
            throw STOMPException.STOMP_SEND_ALL_GROUP_MESSAGE_FAILED
        }
    }

    fun sendTargetGroup(groupMessage: GroupMessageDTO) {
        kotlin.runCatching {
            val groupId = groupMessage.groupId
            DarcyLogger.warn("群消息 --> $groupId $groupMessage")
            // Spring STOMP 广播 Broadcast - 只发送给指定群组的订阅者
            websocket.convertAndSend("$SEND_TARGET_GROUP_MESSAGE_URL_PREFIX$groupId", groupMessage)
            val sender = userService.queryUserById(groupMessage.senderId)
            val group = groupService.queryGroupById(groupMessage.groupId)
            val savedMessage = groupMessageService.createMessage(groupMessage.toEntity(sender, group))
            val members = groupService.queryAllGroupMembersById(groupId)
            members.forEach { member ->
                if (member.id != groupMessage.senderId) {
                    messageReadStatusService.senderCreateOrUpdateReadStatus(
                        msgId = savedMessage.msgId,
                        userId = member.id,
                        conversationType = Conversation.ConversationType.GROUP,
                        targetId = groupId,
                        isRead = false
                    )
                }
            }
        }.onSuccess {
            DarcyLogger.info("发送群消息成功")
        }.onFailure {
            DarcyLogger.error("发送群消息失败: ${it::class.java.simpleName} ${it.message}")
            it.printStackTrace()
            throw STOMPException.STOMP_SEND_TARGET_GROUP_MESSAGE_FAILED
        }
    }

    fun sendMessageReadStatus(
        fromUserId: Long,
        toUserId: Long,
        url: String,
        messageReadStatus: ReceiverMessageReadStatusMarkRequestDTO,
    ) {
        kotlin.runCatching {
//            val messageReadStatus = JsonUtil.fromJson(message, ReceiverMessageReadStatusMarkRequestDTO::class.java)
//                ?: throw STOMPException.STOMP_PRIVATE_MESSAGE_READ_STATUS_FORMAT_ERROR
            DarcyLogger.warn("消息已读状态消息 --> $messageReadStatus")
            val userId = messageReadStatus.userId
            val msgIds = messageReadStatus.msgIds
            val updatedCount = messageReadStatusService.receiverMarkMessagesAsRead(userId, msgIds)
            val result = messageReadStatusService.receiverGetMessageListReadStatus(userId, msgIds)
            val headers = mapOf(
                "fromUserId" to fromUserId,
                "toUserId" to toUserId,
                "url" to url
            )
            // Spring STOMP 单播 Unicast
            websocket.convertAndSendToUser(
                messageReadStatus.targetName,
                SEND_PRIVATE_MESSAGE_READ_URL,
                result.toDTO(),
                headers
            )
        }.onSuccess {
            DarcyLogger.info("发送消息已读状态成功")
        }.onFailure {
            DarcyLogger.error("发送已读状态消息失败: ${it::class.java.simpleName} ${it.message}")
            it.printStackTrace()
            throw STOMPException.STOMP_SEND_PRIVATE_MESSAGE_READ_STATUS_FAILED
        }
    }
}