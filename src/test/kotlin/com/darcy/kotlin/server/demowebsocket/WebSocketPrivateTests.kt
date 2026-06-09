package com.darcy.kotlin.server.demowebsocket

import com.darcy.kotlin.server.demowebsocket.config.JwtToken.JWT_TOKEN
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.PrivateMessageDTO
import com.darcy.kotlin.server.demowebsocket.utils.TimeUtil
import com.darcy.kotlin.server.demowebsocket.utils.TokenUtil
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.CLIENT_SEND_MESSAGE_PREFIX
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.config.StompWebsocketConfig.Companion.SEND_PRIVATE_MESSAGE_URL
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.lang.Nullable
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WebSocketPrivateTests {

    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val JWT_TOKEN_2 =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKZXJyeSIsInRva2VuVmVyc2lvbiI6MSwiaWF0IjoxNzc4MTM3MjAwLCJleHAiOjE3Nzg3NDIwMDB9.81L6M6jrAoX1YioEdJlaYgeQpeDSgGbZasDFwv1WXKJIYb9iZDVlrApd8iI3ylCquzncpYlhN4K4Z_-FMJKTPw"

    private fun createJwtToken1(): String {
        return JWT_TOKEN
    }

    private fun createJwtToken2(): String {
        return JWT_TOKEN_2
    }

    private fun createWebSocketStompClientSession(jwtToken: String): Pair<WebSocketStompClient, StompSession> {
        // 1. 创建客户端
        val transports = listOf(WebSocketTransport(StandardWebSocketClient()))
        val sockJsClient = SockJsClient(transports)
        val stompClient = WebSocketStompClient(sockJsClient)
        // 关键：配置 TaskScheduler
        val taskScheduler = ThreadPoolTaskScheduler().apply {
            poolSize = 4
//            threadNamePrefix = "websocket-receipt-scheduler-"
            isDaemon = true
            initialize()
        }
        stompClient.taskScheduler = taskScheduler
        // 设置消息转换器
        val converter = MappingJackson2MessageConverter()
        stompClient.messageConverter = converter
        // 开启底层调试
        stompClient.receiptTimeLimit = 10_000  // RECEIPT 超时时间（如果你的版本支持）

        // 2. 准备头部
        val webSocketHeaders = WebSocketHttpHeaders()
        webSocketHeaders[TokenUtil.TOKEN_HEADER] = jwtToken
        val stompHeaders = StompHeaders()
        // 可以设置 STOMP 特定的头部
        stompHeaders[TokenUtil.TOKEN_HEADER] = jwtToken
        val url = "http://localhost:$port/stomp-sockjs"  // 注意 SockJS 用 http
        val sessionFuture = CompletableFuture<StompSession>()

        // 3. 使用正确的 connectAsync 重载
        stompClient.connectAsync(url, webSocketHeaders, stompHeaders, object : StompSessionHandlerAdapter() {
//            override fun getPayloadType(headers: StompHeaders): Type {
//                return PrivateMessageDTO::class.java
//            }

            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                println("Connected! Session: ${session.sessionId}")
                println("Connected headers: $connectedHeaders")
                // 给 future 设置值
                sessionFuture.complete(session)
            }

            override fun handleException(
                session: StompSession,
                @Nullable command: StompCommand?,
                headers: StompHeaders,
                payload: ByteArray,
                exception: Throwable
            ) {
                println("Exception: ${exception.message}")
            }

            override fun handleTransportError(session: StompSession, exception: Throwable) {
                println("Transport error: ${exception.message}")
            }
        })

        val session = sessionFuture.get(5, TimeUnit.SECONDS)
        return Pair(stompClient, session)
    }

    // STOMP 是 Simple Text Oriented Messaging Protocol
    // 简单文本定向消息协议。
    @Test
    fun `test-private-websocket-stomp`() {
        // 创建两个会话
        val pair1 = createWebSocketStompClientSession(createJwtToken1())
        val stompClient1 = pair1.first
        val session1 = pair1.second
        val pair2 = createWebSocketStompClientSession(createJwtToken2())
        val stompClient2 = pair2.first
        val session2 = pair2.second

        // 设置 receipt 确认帧
        session1.setAutoReceipt(true)
        session2.setAutoReceipt(true)

        // 等待连接
        try {
            // 使用 CountDownLatch 等待消息接收
            val messageLatch = CountDownLatch(1)
            val receiptLatch = CountDownLatch(1)

            // 订阅主题
            val url = CLIENT_SEND_MESSAGE_PREFIX + SEND_PRIVATE_MESSAGE_URL
            val queueSubscription = session1.subscribe(url, object : StompSessionHandlerAdapter() {
                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    println("1--Received Queue headers-->: ${headers.entries}")
                    println("1--Received Queue message-->: $payload ")
                }

                override fun getPayloadType(headers: StompHeaders): Type {
                    return PrivateMessageDTO::class.java
                }
            })
            queueSubscription.addReceiptTask(Runnable {
                println("🎉 [RECEIPT SUCCESS] 订阅1的确认帧已被服务器接收!")
            })
            queueSubscription.addReceiptLostTask {
                println("❌ [RECEIPT FAILURE] 订阅1的确认帧失败!")
            }
            Thread.sleep(500) // 等待订阅确认日志

            val queueSubscription2 = session2.subscribe(url, object : StompSessionHandlerAdapter() {
                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    println("2--Received Queue headers-->: ${headers.entries}")
                    println("2--Received Queue message-->: $payload")
                    // 收到消息后 调用 messageLatch 的 countDown
                    messageLatch.countDown()
                }

                override fun getPayloadType(headers: StompHeaders): Type {
                    return PrivateMessageDTO::class.java
                }
            })
            queueSubscription2.addReceiptTask(Runnable {
                println("🎉 [RECEIPT SUCCESS] 订阅2的确认帧已被服务器接收!")
            })
            queueSubscription2.addReceiptLostTask {
                println("❌ [RECEIPT FAILURE] 订阅2的确认帧失败!")
            }
            Thread.sleep(500) // 等待订阅确认日志

            // Alice发送消息
            val aliceIdentityPublicKey = "302a300506032b656e0321003d4fa7151d41dd6242145c651e3b26f2c8c2b285e28f6843bffd82d6232d832a"
            val aliceEphemeralPublicKey = "302a300506032b656e032100a0b9b7d397986c7c2c492d6b03039eeccba10c32ad86538546a46fb2b0b07226"
            val sendHeaders = StompHeaders().apply {
                destination = "/app/sendPrivateMessage"
                receipt = "receipt-1-${System.currentTimeMillis()}"
//                receiptId = "receipt-1${System.currentTimeMillis()}"
                set("identityPublicKey", aliceIdentityPublicKey) // 仅第一条消息发送
                set("oneTimePublicKeyId", "1") // 仅第一条消息发送
                set("dhPublicKey", aliceEphemeralPublicKey) // 每一条消息都发送
            }
            val privateMessage = PrivateMessageDTO(
                msgId = "",
                senderId = 13,
                senderName = "Tom",
                receiverId = 14,
                receiverName = "Jerry",
                content = "测试消息1",
                msgType = "TEXT",
                sendTime = TimeUtil.getCurrentTimeString(),
                isRecalled = false
            )
            // 发送消息 获取返回值用于处理Receipt确认帧
            val sendReceipt = session1.send(sendHeaders, privateMessage)
            // *** 核心：注册异步接收任务以记录日志 ***
            sendReceipt.addReceiptTask(Runnable {
                println("🎉 [RECEIPT SUCCESS] 发送操作成功! Receipt ID: ${sendReceipt.receiptId}")
                receiptLatch.countDown()
            })
            sendReceipt.addReceiptLostTask {
                println("❌ [RECEIPT FAILURE] 发送操作失败! Receipt ID: ${sendReceipt.receiptId}")
            }

            // 等待消息接收与确认帧结果
            // 等待消息接收（最多等待 5 秒）
            val received = messageLatch.await(5, TimeUnit.SECONDS)
            if (received) {
                println("✓ 测试通过：成功接收消息")
            } else {
                println("✗ 测试失败：未在超时时间内接收到消息")
            }
            assertEquals(true, received, "接收消息错误")

            val receiptReceived = receiptLatch.await(10, TimeUnit.SECONDS)
            if (receiptReceived) {
                println("✓ 测试通过：成功接收消息与确认帧")
            } else {
                println("✗ 测试失败：未在超时时间内收到确认帧，但消息已成功投递")
            }
            assertEquals(true, receiptReceived, "未在超时时间内收到确认帧，但消息已成功投递")

            // 清理
            queueSubscription.unsubscribe()
            queueSubscription2.unsubscribe()

            // 优雅断开连接，等待服务端的 RECEIPT 确认帧
            session1.disconnect()
            session2.disconnect()
            // 等待断开连接
            Thread.sleep(100)
            println("✓ Disconnected")

        } catch (e: Exception) {
            e.printStackTrace()
            println("✗ Connection error: ${e.javaClass.simpleName} ${e.message}")
        } finally {
            stompClient1.stop()
            stompClient2.stop()
        }
    }

}