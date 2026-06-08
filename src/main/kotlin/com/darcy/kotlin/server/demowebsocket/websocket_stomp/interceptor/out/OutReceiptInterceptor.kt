package com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.out

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.exception.ExceptionChecker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

/**
 * Out拦截器 拦截服务端发出的消息
 */
@Component
class OutReceiptInterceptor @Autowired constructor(
    @Lazy
    val simpUserRegistry: SimpUserRegistry
) : ChannelInterceptor {
    companion object {
        private const val TAG = "StompOutInterceptor"
    }

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        return super.preSend(message, channel)
    }

    override fun postSend(message: Message<*>, channel: MessageChannel, sent: Boolean) {
        super.postSend(message, channel, sent)
    }

    override fun afterSendCompletion(message: Message<*>, channel: MessageChannel, sent: Boolean, ex: Exception?) {
        if (ex != null) {
            handleSendError(ex, message);
        }
//        val accessor = StompHeaderAccessor.wrap(message)
        val accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor::class.java
        ) ?: return
        accessor.run {
            DarcyLogger.info("$TAG preSend command=${command?.name}")
            when (command) {
                StompCommand.RECEIPT -> {
                    val sessionId = accessor.sessionId
                    val user = accessor.user
                    val receipt = accessor.receipt ?: ""
                    DarcyLogger.info("$TAG RECEIPT 帧已发送:  user=${user?.name} sessionId=$sessionId,receipt=$receipt")
                }
                StompCommand.CONNECTED -> {
                }

                StompCommand.MESSAGE -> {
                    val sessionId = accessor.sessionId
                    val user = accessor.user
                    DarcyLogger.info("$TAG MESSAGE 帧已发送: sessionId=$sessionId, user=${user?.name}")
                }

                StompCommand.ERROR -> {

                }

                else -> {

                }
            }
        }
    }

    private fun handleSendError(exception: Exception, message: Message<*>) {
        DarcyLogger.error("$TAG 错误: ${message.headers} ${message.payload}", exception)
        if (ExceptionChecker.isIgnorableException(exception)){
            DarcyLogger.error("$TAG 发送消息异常 忽略连接已关闭的异常: ${exception::class.java.simpleName} ${exception.message}")
            return
        } else {
            throw exception
        }
    }


    private fun userCount() {
        // 注意：如果这里要用 SimpUserRegistry，记得延迟注入
        val count = simpUserRegistry.userCount
        DarcyLogger.info("$TAG 当前在线人数：$count")
    }
}