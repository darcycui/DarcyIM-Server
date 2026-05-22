package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.exception.code700.ConversationException
import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.http.repository.ConversationRepository
import com.darcy.kotlin.server.demowebsocket.utils.UUIdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConversationService @Autowired constructor(
    private val conversationRepository: ConversationRepository,
    private val userService: UserService,
    private val friendshipService: FriendshipService,
    private val idGenerator: UUIdGenerator,
    private val groupService: GroupService,
) {
    fun createConversation(
        userId: Long,
        conversationType: Conversation.ConversationType,
        targetId: Long
    ): Conversation {
        // 业务参数校验
        val user = userService.queryUserById(userId)
        validateTarget(userId, conversationType, targetId)
        // 检查会话是否存在
        val existingConversation = conversationRepository.findByUserIdAndConversationTypeAndTargetId(
            userId,
            conversationType,
            targetId
        )
        if (existingConversation != null) {
            return existingConversation
        }
        val conversation = Conversation(
            // 会话ID 唯一
            conversationId = idGenerator.nextConversationId(),
            user = user,
            conversationType = conversationType,
            targetId = targetId
        )
        return conversationRepository.save(conversation)
    }

    private fun validateTarget(userId: Long, conversationType: Conversation.ConversationType, targetId: Long) {
        when (conversationType) {
            Conversation.ConversationType.PRIVATE -> {
                val targetUser = userService.queryUserById(targetId)
                val isFriend = friendshipService.isFriend(userId, targetId)
                if (!isFriend) {
                    throw UserException.FRIENDSHIP_NOT_EXIST
                }
            }

            Conversation.ConversationType.GROUP -> {
                // TODO: 验证群组是否存在
                val group = groupService.queryGroupById(targetId)
            }
        }
    }

    fun queryConversations(userId: Long): List<Conversation> {
        return conversationRepository.findByUserId(userId)
    }

    fun queryOneConversation(conversationId: Long): Conversation {
        val conversation = conversationRepository.findById(conversationId)
        if (conversation.isEmpty) {
            throw ConversationException.CONVERSATION_NOT_EXIST
        }
        return conversation.get()
    }

    fun deleteByUserIdAndTargetId(userId: Long, friendId: Long): Int {
        return conversationRepository.deleteByUserIdAndTargetId(userId, friendId)
    }
}