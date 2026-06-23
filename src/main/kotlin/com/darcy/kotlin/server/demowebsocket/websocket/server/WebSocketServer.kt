//package com.darcy.kotlin.server.demowebsocket.websocket.server
//
//import com.alibaba.fastjson.JSONObject
//import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
//import com.darcy.kotlin.server.demowebsocket.websocket.config.WsConfigurator
//import com.darcy.kotlin.server.demowebsocket.websocket.domain.StrChatEntity
//import com.darcy.kotlin.server.demowebsocket.websocket.helper.ActiveCheckHelper
//import com.darcy.kotlin.server.demowebsocket.websocket.helper.SendHelper
//import jakarta.websocket.OnClose
//import jakarta.websocket.OnError
//import jakarta.websocket.OnMessage
//import jakarta.websocket.OnOpen
//import jakarta.websocket.Session
//import jakarta.websocket.server.PathParam
//import jakarta.websocket.server.ServerEndpoint
//import org.springframework.stereotype.Component
//import java.util.concurrent.ConcurrentHashMap
//
///**
// * 每一个连接对应一个WebSocketServer对象
// */
//@ServerEndpoint(
//    "/person/{userId}",
//    configurator = WsConfigurator::class // 添加心跳配置器
//)
//@Component
//class WebSocketServer {
//    companion object {
//        /**
//         * 存储所有连接 注意这个是map全局的要使用伴生对象
//         */
//        @JvmStatic
//        private val SOCKET_MAP: MutableMap<String, WebSocketServer> = ConcurrentHashMap<String, WebSocketServer>()
//        fun getSocketMap(): Map<String, WebSocketServer> {
//            return SOCKET_MAP
//        }
//
//        private val activeCheckHelper = ActiveCheckHelper
//    }
//
//    private var session: Session? = null
//    private var userId: String? = null
//
//    fun getSession(): Session? {
//        return session
//    }
//
//    @OnOpen
//    fun open(session: Session, @PathParam("userId") userId: String) {
//        this.session = session
//        SOCKET_MAP[userId] = this
//        this.userId = userId
//        activeCheckHelper.updateLastActiveTime(userId)
//        logI("用户${session.id} $userId 连接成功, 当前连接总人数为${SOCKET_MAP.size}")
//    }
//
//    @OnClose
//    fun close(session: Session, @PathParam("userId") userId: String) {
//        SOCKET_MAP.remove(userId)
//        activeCheckHelper.removeLastActiveTime(userId)
//        logI("用户${session.id} $userId 断开连接, 当前连接总人数为${SOCKET_MAP.size}")
//    }
//
//    @OnMessage
//    fun onMessage(message: String, session: Session) {
//        try {
//            logI("收到用户${session.id} $userId 的消息:$message")
//            activeCheckHelper.updateLastActiveTime(userId ?: "")
//            if (message == "ping") {
//                SendHelper.sendPong(session)
//                return
//            }
//            // fastjson 解析 message 为 ChatEntityStr
//            val chatEntity = JSONObject.parseObject(message, StrChatEntity::class.java)
//            val socketServer = SOCKET_MAP[chatEntity.to]
//            if (socketServer == null) {
//                logI("用户${chatEntity.to}不在线")
//                return
//            }
//            SendHelper.sendToPersonStr(message, socketServer.session)
//        } catch (e: Exception) {
//            logI("用户${session.id} $userId 发送的消息格式有误")
//            e.printStackTrace()
//            disconnectUser(session.id)
//        }
//    }
//
//    @OnError
//    fun onError(session: Session, error: Throwable) {
//        logI("用户${session.id} $userId 发生错误")
//        error.printStackTrace()
//    }
//
//    // 添加主动断开方法
//    fun disconnectUser(userId: String) {
//        SOCKET_MAP[userId]?.let {
//            logI("主动断开用户 $userId ${session?.id} 当前连接总人数为${SOCKET_MAP.size}")
//            it.session?.close()
//            SOCKET_MAP.remove(userId)
//            activeCheckHelper.removeLastActiveTime(userId)
//        }
//    }
//}