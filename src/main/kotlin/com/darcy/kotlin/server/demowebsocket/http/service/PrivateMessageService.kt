package  com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.conversation.Conversation
import com.darcy.kotlin.server.demowebsocket.domain.table.message.PrivateMessage
import com.darcy.kotlin.server.demowebsocket.exception.code700.ConversationException
import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.http.repository.PrivateMessageRepository
import com.darcy.kotlin.server.demowebsocket.utils.UUIdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PrivateMessageService @Autowired constructor(
    private val privateMessageRepository: PrivateMessageRepository,
    @Lazy
    private val conversationService: ConversationService,
    @Lazy
    private val friendshipService: FriendshipService,
    private val userService: UserService,
    private val idGenerator: UUIdGenerator
) {
    fun createMessage(
        senderId: Long,
        receiverId: Long,
        conversationId: Long,
        content: String,
        msgId:String,
    ): PrivateMessage {
        val sender = userService.queryUserById(senderId)
        val receiver = userService.queryUserById(receiverId)
        validateFriendship(senderId, receiverId)
        validateConversation(conversationId, senderId, receiverId)
        val message = PrivateMessage(
            // 单聊消息ID 唯一
            msgId = msgId,
            sender = sender,
            receiver = receiver,
            content = content,
            sendTime = LocalDateTime.now()
        )
        return createMessage(message)
    }

    fun createMessage(message: PrivateMessage): PrivateMessage {
        return privateMessageRepository.save(message)
    }

    private fun validateConversation(conversationId: Long, senderId: Long = 0, receiverId: Long = 0) {
        val conversation = conversationService.queryOneConversation(conversationId)
        if (conversation.conversationType != Conversation.ConversationType.PRIVATE) {
            throw ConversationException.CONVERSATION_TYPE_ERROR
        }
        val isSenderValidate = conversation.user.id == senderId && conversation.targetId == receiverId
        val isReceiverValidate = conversation.targetId == senderId && conversation.user.id == receiverId
        if (senderId > 0 && receiverId > 0 && isSenderValidate.not() && isReceiverValidate.not()) {
            throw ConversationException.CONVERSATION_MEMBER_ERROR
        }
    }

    private fun validateFriendship(senderId: Long, receiverId: Long) {
        if (!friendshipService.isFriend(senderId, receiverId)) {
            throw UserException.FRIENDSHIP_NOT_EXIST
        }
    }

    fun queryBothMessagesAllByConversation(conversationId: Long): List<PrivateMessage> {
        validateConversation(conversationId)
        val conversation = conversationService.queryOneConversation(conversationId)
        val senderId = conversation.user.id
        val receiverId = conversation.targetId
        return privateMessageRepository.findBothMessagesAll(senderId, receiverId)
    }

    fun queryBothMessagesPageByConversation(conversationId: Long, page: Int, size: Int): Page<PrivateMessage> {
        validateConversation(conversationId)
        val conversation = conversationService.queryOneConversation(conversationId)
        val senderId = conversation.user.id
        val receiverId = conversation.targetId
        val pageable = PageRequest.of(page, size)
        val result =  privateMessageRepository.findBothMessagesPage(senderId, receiverId, pageable)
        val reversedContent = result.content.reversed()
        val reversedResult = PageImpl(reversedContent, result.pageable, result.totalElements)
        return reversedResult
        // 获取分页数据 result
        // result.content  // 当前页的消息列表
        // result.totalPages  // 总页数
        // result.totalElements  // 总记录数
        // result.number  // 当前页码（从 0 开始）
    }

    fun deleteByUserIdAndFriendId(userId: Long, friendId: Long): Int {
        return privateMessageRepository.deleteByUserIdAndFriendId(userId, friendId)
    }
}