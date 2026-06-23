package com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.`in`

import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logI
import com.darcy.kotlin.server.demowebsocket.log.logW
import com.darcy.kotlin.server.demowebsocket.utils.JsonUtil
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.lang.Exception

/**
 * In拦截器 拦截服务器收到的消息
 */
@Component
class InDecryptInterceptor : ChannelInterceptor {
    companion object {
        private val TAG = InDecryptInterceptor::class.java.simpleName
    }

    /**
     * 关键是要理解：preSend 是相对于通道而言的,入站和出站都会触发
     * 当消息进入 clientInboundChannel 时，preSend 是服务器接收消息之前的拦截
     * 当消息进入 clientOutboundChannel 时，preSend 是服务器发送消息之前的拦截
     *
     */
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        logI("$TAG 拦截器 preSend")
        val accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor::class.java
        ) ?: run {
            logW("$TAG 拦截器 preSend 获取消息头失败")
            return message
        }
        val destination = accessor.destination
        val receipt = accessor.receipt
        val fromUserId: Long = accessor.getFirstNativeHeader("fromUserId")?.toLongOrNull() ?: 0
        val toUserId: Long = accessor.getFirstNativeHeader("toUserId")?.toLongOrNull() ?: 0
        val url = accessor.getFirstNativeHeader("url") ?: ""
        logI("$TAG destination: $destination, receipt: $receipt, fromUserId: $fromUserId, url: $url")
        // 只处理特定的消息目的地
        val isClientMessage = destination?.startsWith(StompWebsocketConfig.CLIENT_SEND_MESSAGE_PREFIX) == true
        if (isClientMessage) {
            val payload = message.payload
            logW("$TAG 入站消息（客户端发送到服务器）payload type: ${payload::class.java.simpleName}")
            if (payload is ByteArray) {
                logI("$TAG 需要解密 fromUserId=$fromUserId")
                val decryptedPayload = ChaCha20TransportCipher.decrypt(
                    userId = fromUserId,
                    ciphertext = payload.decodeToString().hexStrToBytes(), // 密文是16进制字符串
                    aad = "WS:$url".toByteArray()
                )
                val privateMessageDTO = JsonUtil.fromJson(
                    decryptedPayload.decodeToString(), PrivateMessageDTO::class.java
                ) ?: PrivateMessageDTO(content = "解密失败")
                // 解密成功后，将解密后的消息重新封装并返回
                val newMessage = MessageBuilder.createMessage(
                    decryptedPayload,
                    message.headers
                )
                return newMessage
            } else {
                logW("$TAG 未知的消息类型 payload type: ${payload::class.java.simpleName}")
                return message
            }
        } else {
            return message
        }
    }

    override fun postSend(message: Message<*>, channel: MessageChannel, sent: Boolean) {
        super.postSend(message, channel, sent)
    }

    override fun afterSendCompletion(message: Message<*>, channel: MessageChannel, sent: Boolean, ex: Exception?) {
        super.afterSendCompletion(message, channel, sent, ex)
    }

    override fun preReceive(channel: MessageChannel): Boolean {
        return super.preReceive(channel)
    }

    override fun postReceive(message: Message<*>, channel: MessageChannel): Message<*>? {
        return super.postReceive(message, channel)
    }

    override fun afterReceiveCompletion(message: Message<*>?, channel: MessageChannel, ex: Exception?) {
        super.afterReceiveCompletion(message, channel, ex)
    }

}