package com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.`in`

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.lang.Exception

/**
 * In拦截器 拦截服务器收到的 确认帧
 */
@Component
class InReceiptInterceptor : ChannelInterceptor {
    companion object {
        private val TAG = InReceiptInterceptor::class.java.simpleName
    }


    @Autowired
    @Lazy
    @Qualifier("clientOutboundChannel")
    private lateinit var clientOutboundChannel: MessageChannel
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        when (accessor.command) {
            StompCommand.SEND -> {
                val receipt = accessor.receipt
                if (StringUtils.hasText(receipt)) {
                    println("$TAG 收到带确认帧的消息: command=${accessor.command}, receipt=$receipt, destination=${accessor.destination}")
                }
            }

            StompCommand.SUBSCRIBE -> {
                val receipt = accessor.receipt
                if (StringUtils.hasText(receipt)) {
                    println("$TAG 收到带确认帧的订阅: command=${accessor.command}, receipt=$receipt, destination=${accessor.destination}")
                }
            }

            StompCommand.UNSUBSCRIBE -> {
                val receipt = accessor.receipt
                if (StringUtils.hasText(receipt)) {
                    println("$TAG 收到带确认帧的取消订阅: command=${accessor.command}, receipt=$receipt")
                }
            }

            StompCommand.DISCONNECT -> {
                val receipt = accessor.receipt
                if (StringUtils.hasText(receipt)) {
                    println("$TAG 收到带确认帧的断开连接: command=${accessor.command}, receipt=$receipt")
                }
            }

            else -> {}
        }

        return message
    }

    override fun postSend(message: Message<*>, channel: MessageChannel, sent: Boolean) {
        super.postSend(message, channel, sent)
    }

    override fun afterSendCompletion(message: Message<*>, channel: MessageChannel, sent: Boolean, ex: Exception?) {
        val accessor = StompHeaderAccessor.wrap(message)
        val receipt = accessor.receipt ?: ""

        if (StringUtils.hasText(receipt)) {
            if (sent) {
                println("$TAG 消息已成功处理，准备发送确认帧: receipt=$receipt")
                // 这里可以触发业务逻辑，如记录日志到数据库
                // 这里手动发送 RECEIPT 帧
                sendReceiptIfNeeded(message)
            } else if (ex != null) {
                println("$TAG 消息处理失败: receipt=$receipt, error=${ex.message}")
            }
        } else {
            println("$TAG 确认帧未指定，忽略: command=${accessor.command}, destination=${accessor.destination}")
        }
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

    /**
     * 如果需要 receipt，则发送 RECEIPT 帧
     */
    private fun sendReceiptIfNeeded(message: Message<*>) {
        val accessor = StompHeaderAccessor.wrap(message)
        val receipt = accessor.receipt ?: ""

        if (StringUtils.hasText(receipt)) {
            sendReceiptFrame(receipt, accessor)
        } else {
            println("$TAG 确认帧 null")
        }
    }

    /**
     * 发送 STOMP RECEIPT 帧到客户端
     */
    private fun sendReceiptFrame(receiptId: String, originalAccessor: StompHeaderAccessor) {
        try {
            val sessionId = originalAccessor.sessionId
            println("$TAG 开始构建 RECEIPT 帧: receipt-id=$receiptId, sessionId=$sessionId")

            // 创建 RECEIPT 帧的 HeaderAccessor
            val receiptAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT)
            receiptAccessor.receiptId = receiptId
            receiptAccessor.sessionId = sessionId

            // 设置原生 STOMP 头
            receiptAccessor.setNativeHeader("receipt-id", receiptId)

            // 保持可变性
            receiptAccessor.setLeaveMutable(true)

            // RECEIPT 帧的 body（STOMP 协议的 RECEIPT 帧通常带有空 body）
            val payload = ByteArray(0)

            // 构建消息
            val receiptMessage = MessageBuilder.createMessage(
                payload,
                receiptAccessor.messageHeaders
            )

            // 发送到客户端输出通道
            clientOutboundChannel.send(receiptMessage)

            println("$TAG RECEIPT 帧已发送: receipt-id=$receiptId, sessionId=$sessionId")
            println("$TAG 帧头信息: ${receiptAccessor.toNativeHeaderMap()}")

        } catch (e: IllegalStateException) {
            // 会话已关闭，忽略此异常
            if (e.message?.contains("Cannot send a message when session is closed") == true) {
                println("$TAG 会话已关闭，无法发送 RECEIPT 帧: receipt-id=$receiptId, error=${e.message}")
                return
            } else {
                println("$TAG 发送 RECEIPT 帧失败: ${e.message}")
                e.printStackTrace()
            }
        } catch (e: Exception) {
            println("$TAG 发送 RECEIPT 帧失败: ${e.message}")
            e.printStackTrace()
        }
    }

}