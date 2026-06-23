package com.darcy.kotlin.server.demowebsocket.websocket_stomp.exception

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logE
import com.darcy.kotlin.server.demowebsocket.log.logI
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.MessagingException
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.handler.WebSocketHandlerDecorator
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory

/**
 * websocket 异常处理
 */
@Configuration
@EnableWebSocket
class WebSocketExceptionDecorator : WebSocketHandlerDecoratorFactory {
    companion object {
        private val TAG = WebSocketExceptionDecorator::class.java.simpleName
    }

    override fun decorate(handler: WebSocketHandler): WebSocketHandler {
        return object : WebSocketHandlerDecorator(handler) {
            override fun afterConnectionEstablished(session: WebSocketSession) {
                super.afterConnectionEstablished(session)
                try {
                    logI("websocket 连接已建立: ${session.id}")
                    // 二次检查认证信息，如果userName为空 则关闭连接
                    val userName = session.attributes["userName"] as? String
                    if (userName.isNullOrBlank()) {
                        logE("$TAG 用户 userName 为空，拒绝连接并关闭会话 sessionId=${session.id}")
                        session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Authentication required"))
                        throw MessagingException("$TAG Authentication 认证失败2: userName 为空")
                    }
                } catch (e: Exception) {
                    logE("websocket 创建连接异常:${e.message} sessionId=${session.id}")
                    e.printStackTrace()
                }
            }

            override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
                logI("websocket 断开连接: ${session.id} 状态: $closeStatus")
                try {
                    super.afterConnectionClosed(session, closeStatus)
                } catch (e: Exception) {
                    if (ExceptionChecker.isIgnorableException(e)) {
                        // 忽略连接已关闭的异常
                        logE("websocket 断开连接 忽略连接已关闭的异常: ${e::class.java.simpleName} ${e.message}")
                    } else {
                        logE("websocket 断开连接异常: ${e.message} sessionId=${session.id}")
                        e.printStackTrace()
                    }
                }
            }

            override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
                logI("websocket 处理消息: ${session.id}")
                try {
                    super.handleMessage(session, message)
                } catch (e: Exception) {
                    if (ExceptionChecker.isIgnorableException(e)) {
                        // 忽略连接已关闭的异常
                        logE("websocket 处理消息 忽略连接已关闭的异常: ${e::class.java.simpleName} ${e.message}")
                    } else {
                        logE("websocket 处理消息异常: ${e.message} sessionId= ${session.id}")
                        e.printStackTrace()
                    }
                }
            }

            override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
                logE("websocket 处理错误: ${exception.message} sessionId=${session.id}")
                try {
                    if (ExceptionChecker.isIgnorableException(exception)) {
                        // 忽略连接已关闭的异常
                        logE("websocket 处理错误 忽略连接已关闭的异常: ${exception::class.java.simpleName} ${exception.message}")
                    } else {
                        super.handleTransportError(session, exception)
                    }
                } catch (e: Exception) {
                    logE("websocket 处理错误异常: ${e.message} sessionId=${session.id}")
                    e.printStackTrace()
                }
            }
        }
    }

}
