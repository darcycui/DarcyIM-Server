package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.message.GroupMessage
import com.darcy.kotlin.server.demowebsocket.exception.code700.ConversationException
import com.darcy.kotlin.server.demowebsocket.exception.code900.GroupException
import com.darcy.kotlin.server.demowebsocket.http.repository.GroupMessageRepository
import com.darcy.kotlin.server.demowebsocket.utils.UUIdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GroupMessageService @Autowired constructor(
    private val groupMessageRepository: GroupMessageRepository,
    private val userService: UserService,
    private val groupService: GroupService,
    private val groupMemberService: GroupMemberService,
    private val conversationService: ConversationService,
    private val idGenerator: UUIdGenerator,
) {
    fun createMessage(
        senderId: Long,
        groupId: Long,
        conversationId: Long,
        content: String,
        msgId: String,
    ): GroupMessage {
        val sender = userService.queryUserById(senderId)
        val group = groupService.queryGroupById(groupId)
        validateConversation(conversationId, senderId, groupId)
        validateGroupMember(senderId, groupId)
        val message = GroupMessage(
            // 群消息ID 唯一
            msgId = msgId,
            sender = sender,
            group = group,
            content = content,
            sendTime = LocalDateTime.now()
        )
        return createMessage(message)
    }

    fun createMessage(groupMessage: GroupMessage): GroupMessage {
        return groupMessageRepository.save(groupMessage)
    }

    private fun validateGroupMember(senderId: Long, groupId: Long) {
        val isGroupMember = groupMemberService.isGroupMember(senderId, groupId)
        if (!isGroupMember) {
            throw GroupException.USER_NOT_GROUP_MEMBER
        }
    }

    private fun validateConversation(conversationId: Long, senderId: Long, groupId: Long) {
        val conversation = conversationService.queryOneConversation(conversationId)
        if (conversation.conversationType != Conversation.ConversationType.GROUP) {
            throw ConversationException.CONVERSATION_TYPE_ERROR
        }
        if (conversation.targetId != groupId) {
            throw ConversationException.CONVERSATION_MEMBER_ERROR
        }
    }

    fun queryGroupMessages(
        userId: Long,
        groupId: Long,
        conversationId: Long,
        page: Int,
        size: Int
    ): Page<GroupMessage> {
        validateConversation(conversationId, userId, groupId)
        validateGroupMember(userId, groupId)
        val pageable = PageRequest.of(page, size)
        return groupMessageRepository.findGroupMessagePage(groupId, pageable)
    }
}