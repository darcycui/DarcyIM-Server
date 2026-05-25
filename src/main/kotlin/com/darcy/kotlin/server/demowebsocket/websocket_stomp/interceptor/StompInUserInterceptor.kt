package com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor

import com.darcy.kotlin.server.demowebsocket.config.jwt.JwtTokenProvider
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.TokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessagingException
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import java.lang.Exception

/**
 * In拦截器 拦截服务端收到的消息
 */
@Component
class StompInUserInterceptor @Autowired constructor(
    @Lazy
    val simpUserRegistry: SimpUserRegistry,
    val tokenProvider: JwtTokenProvider,
    private val userService: UserService,
) : ChannelInterceptor, ApplicationListener<SessionConnectedEvent> {
    companion object {
        private const val TAG = "StompInUserInterceptor"
    }
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
//        val accessor = StompHeaderAccessor.wrap(message)
        val accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor::class.java
        ) ?: return message
        accessor.run {
            DarcyLogger.info("$TAG preSend command=${command?.name}")
            when (command) {
                StompCommand.CONNECT -> {
                    // websocket connect 方式连接 注册用户名
                    registerUserName(accessor)
                }

                StompCommand.STOMP -> {
                    // websocket STOMP 方式连接 注册用户名
                    registerUserName(accessor)
                }

                StompCommand.SUBSCRIBE -> {
                    val userId = user?.name ?: ""
                    DarcyLogger.info("$TAG 用户$userId 订阅了 $destination")
                }

                StompCommand.DISCONNECT -> {
                    val userId = user?.name ?: ""
                    DarcyLogger.info("$TAG 用户$userId 下线了")
                    // 更新用户的最后活跃时间（即离线时间）
                    userService.updateLastActiveTime(userId)
                    userCount()
                }

                StompCommand.UNSUBSCRIBE -> {
                    val userId = user?.name ?: ""
                    DarcyLogger.info("$TAG 用户$userId 取消订阅了 $destination")
                }

                StompCommand.SEND -> {
                    val userId = user?.name ?: ""
                    DarcyLogger.info("$TAG 用户$userId 发送了消息到 $destination")
                }

                StompCommand.ACK -> {}
                StompCommand.NACK -> {}
                StompCommand.BEGIN -> {}
                StompCommand.COMMIT -> {}
                StompCommand.ABORT -> {}

                StompCommand.RECEIPT -> {
                    val userId = user?.name ?: ""
                    DarcyLogger.info("$TAG 用户$userId 接收了 RECEIPT 确认帧 $receiptId")
                }

                StompCommand.MESSAGE -> {}
                StompCommand.ERROR -> {
                    val userId = user?.name ?: ""
                    DarcyLogger.info("$TAG 用户$userId 接收到错误帧 $sessionId")
                }

                else -> {}
            }
        }
        return message
    }

    override fun afterSendCompletion(message: Message<*>, channel: MessageChannel, sent: Boolean, ex: Exception?) {
        super.afterSendCompletion(message, channel, sent, ex)
        if (ex != null) {
            val accessor = StompHeaderAccessor.wrap(message)
            DarcyLogger.error("$TAG afterSendCompletion 异常: " +
                    "command=${accessor.command}, " +
                    "sessionId=${accessor.sessionId}", ex)
        }
    }

    /**
     * 从 header 获取用户名
     */
    private fun registerUserName(accessor: StompHeaderAccessor) {
        // 1. 先从 STOMP 头中获取 Authorization
        val token1 = accessor.getFirstNativeHeader(TokenUtil.TOKEN_HEADER) ?: ""
        var userName = tokenProvider.getUsernameFromJWT(TokenUtil.cutOnlyToken(token1))
        DarcyLogger.info("$TAG 从STOMP头获取 userName: $userName")

        // 2. 如果 STOMP 头中没有，则从 WebSocket 握手阶段的 sessionAttributes 中获取
        if (userName.isBlank()) {
            val sessionAttributes = accessor.sessionAttributes ?: emptyMap()
            val token2 = sessionAttributes["userName"]?.toString() ?: ""
            val userName2 = tokenProvider.getUsernameFromJWT(TokenUtil.cutOnlyToken(token2))
            DarcyLogger.info("$TAG 从握手属性获取 userName2: $userName2")
            userName = userName2
        }
        if (userName.isBlank()) {
            DarcyLogger.error("$TAG 用户 userName 为空，拒绝连接并关闭会话 sessionId=${accessor.sessionId}")
            throw MessagingException("$TAG Authentication 认证失败: userName 为空")
        }
        val userExist = userService.isUserExistByName(userName)
        if (userExist.not()) {
            DarcyLogger.error("$TAG 用户 $userName 不存在，拒绝连接并关闭会话 sessionId=${accessor.sessionId}")
            throw MessagingException("Authentication 认证失败1: userName 不存在")
        }
        setupUserNameForSTOMP(accessor, userName)
        DarcyLogger.info("$TAG 用户 $userName 上线了")
    }

    private fun userCount() {
        val usersCount = simpUserRegistry.userCount
        DarcyLogger.info("$TAG 当前在线人数为：$usersCount")
    }

    private fun setupUserNameForSTOMP(accessor: StompHeaderAccessor, userName: String) {
        if (userName.isEmpty() or userName.isBlank()) {
            DarcyLogger.error("$TAG 用户未登录")
        }
        if (verifyUserId(userName).not()) {
            DarcyLogger.error("$TAG 用户 userName：$userName 不合法")
        }
        accessor.user = UserNamePrincipal(userName)
    }

    private fun verifyUserId(userName: String): Boolean {
        // TODO: 验证用户是否合法 token JWT 验证
        return userName.isNotEmpty()
//        return userName.startsWith("test")
    }

    override fun onApplicationEvent(event: SessionConnectedEvent) {
        val sessionId = event.message.headers["simpSessionId"]?.toString() ?: ""
        val user = event.user?.name ?: ""
        DarcyLogger.info("$TAG SessionConnectedEvent: 用户 $user 已完成连接，sessionId: $sessionId")
//        userCount()
    }
}