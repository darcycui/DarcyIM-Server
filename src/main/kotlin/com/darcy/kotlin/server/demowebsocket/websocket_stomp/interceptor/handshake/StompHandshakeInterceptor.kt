package com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.handshake

import com.darcy.kotlin.server.demowebsocket.log.*
import com.darcy.kotlin.server.demowebsocket.utils.TokenUtil
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.util.*

/**
 * 握手拦截器
 */
@Component
class StompHandshakeInterceptor : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        try {
            val uri = request.uri
            logI("WebSocket 握手请求: ${uri.path}")

            val headers = request.headers

            val authHeader = headers.getFirst(TokenUtil.TOKEN_HEADER)
                ?: headers.getFirst("Sec-WebSocket-Protocol")
                ?: ""

            val sessionId = UUID.randomUUID().toString()
            if (authHeader.isBlank()) {
                logW("WebSocket 握手缺少 Authorization header")
                attributes["userName"] = "username:$sessionId"
            } else {
                logI("WebSocket 握手 Authorization: $authHeader")
                attributes["userName"] = authHeader
            }

            attributes["sessionId"] = sessionId
            logI("WebSocket 握手成功，sessionId: $sessionId")

            return true
        } catch (e: Exception) {
            logE("WebSocket 握手失败: ${e.message} sessionId=${e.message}")
            return false
        }
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        if (exception != null) {
            logE("WebSocket 握手后异常: ${exception.message} sessionId=${exception.message}", )
        } else {
            logD("WebSocket 握手完成")
        }
    }
}