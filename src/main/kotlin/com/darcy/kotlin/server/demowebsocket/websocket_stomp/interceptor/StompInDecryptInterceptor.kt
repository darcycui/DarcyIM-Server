package com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor

import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.JsonUtil
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.CLIENT_SEND_MESSAGE_PREFIX
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SERVER_SEND_MESSAGE_PREFIX
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
 * In拦截器 拦截服务器收到的 确认帧
 */
@Component
class StompInDecryptInterceptor : ChannelInterceptor {

    /**
     * 关键是要理解：preSend 是相对于通道而言的,入站和出站都会触发
     * 当消息进入 clientInboundChannel 时，preSend 是服务器接收消息之前的拦截
     * 当消息进入 clientOutboundChannel 时，preSend 是服务器发送消息之前的拦截
     *
     */
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor::class.java
        ) ?: return message
        val destination = accessor.destination
        val receipt = accessor.receipt
        val fromUserId: Long = accessor.getFirstNativeHeader("fromUserId")?.toLongOrNull() ?: 0
        val toUserId: Long = accessor.getFirstNativeHeader("toUserId")?.toLongOrNull() ?: 0
        val url = accessor.getFirstNativeHeader("url") ?: ""
        DarcyLogger.info("destination: $destination, receipt: $receipt, userId: $fromUserId, url: $url")
        // 只处理特定的消息目的地
        if (destination?.startsWith(CLIENT_SEND_MESSAGE_PREFIX) == true) {
            val payload = message.payload
            DarcyLogger.warn("入站消息（客户端发送到服务器）payload type: ${payload::class.java.simpleName}")
            if (payload is String && StringUtils.hasText(receipt).not()) {
                DarcyLogger.info("需要解密")
                val decryptedPayload = ChaCha20TransportCipher.decrypt(
                    userId = fromUserId,
                    ciphertext = payload.hexStrToBytes(),
                    aad = "WS:$url".toByteArray()
                )
                val privateMessageDTO = JsonUtil.fromJson(
                    decryptedPayload.decodeToString(), PrivateMessageDTO::class.java
                ) ?: PrivateMessageDTO(content= "解密失败")
                // 解密成功后，将解密后的消息重新封装并返回
                val newMessage = MessageBuilder.createMessage(
                    privateMessageDTO,
                    message.headers
                )
                return newMessage
            } else {
                DarcyLogger.warn("未知的消息类型 payload type: ${payload::class.java.name}")
                return message
            }
        } else if (destination?.startsWith(SERVER_SEND_MESSAGE_PREFIX) == true) {
            val payload = message.payload
            DarcyLogger.info("出站消息（服务器发送到客户端）payload type: ${payload::class.java.simpleName}")
            if (payload is PrivateMessageDTO && StringUtils.hasText(receipt).not()) {
                // 加密私聊消息
                DarcyLogger.info("需要加密")
                val encryptedPayload = ChaCha20TransportCipher.encrypt(
                    userId = toUserId,
                    plaintext = JsonUtil.toJson(payload).toByteArray(),
                    aad = "WS:$url".toByteArray(),
                )
                val newMessage = MessageBuilder.createMessage(
                    encryptedPayload.bytesToHexStr(),
                    message.headers
                )
                return newMessage
            } else {
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