package com.darcy.kotlin.server.demowebsocket.websocket_stomp.config

import com.darcy.kotlin.server.demowebsocket.websocket_stomp.exception.WebSocketExceptionDecorator
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.handshake.StompHandshakeInterceptor
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.`in`.InDecryptInterceptor
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.`in`.InReceiptInterceptor
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.`in`.InUserInterceptor
import com.darcy.kotlin.server.demowebsocket.websocket_stomp.interceptor.out.OutEncryptInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration

@Configuration
@EnableWebSocketMessageBroker
class StompWebsocketConfig @Autowired constructor(
    val inUserInterceptor: InUserInterceptor,
    val inReceiptInterceptor: InReceiptInterceptor,
    val handshakeInterceptor: StompHandshakeInterceptor,
    val inDecryptInterceptor: InDecryptInterceptor,
    val outEncryptInterceptor: OutEncryptInterceptor,
    val webSocketExceptionDecorator: WebSocketExceptionDecorator
) : WebSocketMessageBrokerConfigurer {
    companion object {
        private const val WEBSOCKET_PATH = "/stomp-ws"
        private const val WEBSOCKET_PATH_JS = "/stomp-sockjs"
        private const val HEARTBEAT_PERIOD = 10_000L

        private const val SUBSCRIBE_GROUP_MESSAGE_PREFIX = "/topic"
        private const val SUBSCRIBE_SINGLE_MESSAGE_PREFIX = "/queue"
        const val CLIENT_SEND_MESSAGE_PREFIX = "/app"
        const val SERVER_SEND_MESSAGE_PREFIX = "/user"
        const val SEND_PRIVATE_MESSAGE_URL = "$SUBSCRIBE_SINGLE_MESSAGE_PREFIX/message"
        const val SEND_PRIVATE_MESSAGE_READ_URL = "$SUBSCRIBE_SINGLE_MESSAGE_PREFIX/message/read"
        const val SEND_ALL_GROUP_MESSAGE_URL = "$SUBSCRIBE_GROUP_MESSAGE_PREFIX/message"
        const val SEND_TARGET_GROUP_MESSAGE_URL_PREFIX = "$SUBSCRIBE_GROUP_MESSAGE_PREFIX/group/message/"

    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        super.configureMessageBroker(registry)
        registry.apply {
            // 订阅路径前缀
            enableSimpleBroker(SUBSCRIBE_GROUP_MESSAGE_PREFIX, SUBSCRIBE_SINGLE_MESSAGE_PREFIX)
            // 单聊: client 发送消息前缀
            setApplicationDestinationPrefixes(CLIENT_SEND_MESSAGE_PREFIX)
            // 单聊: server 发送消息前缀
            setUserDestinationPrefix(SERVER_SEND_MESSAGE_PREFIX)
            // 配置 brokerChannel 拦截器
            configureBrokerChannel().interceptors(
//                outEncryptInterceptor
            )
        }
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        super.registerStompEndpoints(registry)
        registry.apply {
            // 添加原生 STOMP 端点
            addEndpoint(WEBSOCKET_PATH)
                .setAllowedOriginPatterns("*", "null")
                .addInterceptors(handshakeInterceptor)

            // 添加 STOMP 端点，并开启 SockJS 支持
            addEndpoint(WEBSOCKET_PATH_JS)
                .setAllowedOriginPatterns("*", "null")
                .addInterceptors(handshakeInterceptor)
                .withSockJS()
                .setHeartbeatTime(HEARTBEAT_PERIOD)
        }
    }

    /**
     * 收到消息的拦截器
     */
    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        super.configureClientInboundChannel(registration)
        // 配置 TaskScheduler 以启用 receipt 确认帧
        registration.taskExecutor(ThreadPoolTaskExecutor().apply {
            corePoolSize = 4
            maxPoolSize = 4
            queueCapacity = 10_000
        })
        registration.interceptors(
            inUserInterceptor, inReceiptInterceptor, inDecryptInterceptor
        )
    }

    /**
     * 发送消息的拦截器
     */
    override fun configureClientOutboundChannel(registration: ChannelRegistration) {
        registration.taskExecutor(ThreadPoolTaskExecutor().apply {
            corePoolSize = 4
            maxPoolSize = 4
            queueCapacity = 10_000
        })
        registration.interceptors(
            outEncryptInterceptor
        )
    }

    override fun configureMessageConverters(messageConverters: MutableList<MessageConverter>): Boolean {
        return super.configureMessageConverters(messageConverters)
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        super.configureWebSocketTransport(registry)
        registry.apply {
            setMessageSizeLimit(128 * 1024)
            setSendBufferSizeLimit(512 * 1024)
            setSendTimeLimit(10 * 1000)
            // 添加异常处理
            addDecoratorFactory(webSocketExceptionDecorator)
        }
    }
}