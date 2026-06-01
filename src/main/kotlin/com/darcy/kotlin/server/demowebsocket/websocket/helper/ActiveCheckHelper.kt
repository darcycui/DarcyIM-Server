//package com.darcy.kotlin.server.demowebsocket.websocket.helper
//
//import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
//import com.darcy.kotlin.server.demowebsocket.websocket.server.WebSocketServer
//import org.springframework.scheduling.annotation.Scheduled
//import org.springframework.stereotype.Component
//import java.util.concurrent.ConcurrentHashMap
//
///**
// * 定时检测用户是否处于活跃状态
// * 如果某个用户超过60秒没有发送消息，则认为该用户处于不活跃状态，关闭连接
// */
//@Component
//class ActiveCheckHelper {
//    companion object {
//
//        private const val INACTIVE_TIMEOUT = 60_000L
//        private const val INACTIVE_CHECK_INTERVAL = 10_000L
//        private val TAG = ActiveCheckHelper::class.java.simpleName
//
//        // 最后活跃时间记录
//        @JvmStatic
//        private val lastActiveTimeMap = ConcurrentHashMap<String, Long>()
//
//        @JvmStatic
//        fun updateLastActiveTime(userId: String) {
//            if (isUserIdInvalid(userId)) {
//                return
//            }
//            lastActiveTimeMap[userId] = System.currentTimeMillis()
//        }
//
//        @JvmStatic
//        fun removeLastActiveTime(userId: String) {
//            if (isUserIdInvalid(userId)) {
//                return
//            }
//            lastActiveTimeMap.remove(userId)
//        }
//
//        @JvmStatic
//        private fun isUserIdInvalid(userId: String): Boolean {
//            return userId.isEmpty() or userId.isBlank()
//        }
//    }
//
//    // 定时任务（需在Spring Boot启动类添加@EnableScheduling）
//    @Scheduled(fixedRate = INACTIVE_CHECK_INTERVAL) // 每30秒扫描一次
//    fun checkInactiveConnections() {
//        DarcyLogger.warn("$TAG 扫描不活跃连接")
//        val webSocketServerMap: Map<String, WebSocketServer> = WebSocketServer.getSocketMap()
//        val now = System.currentTimeMillis()
//        webSocketServerMap.keys.iterator().forEach { userId ->
//            DarcyLogger.info("$TAG userId:$userId")
//            lastActiveTimeMap[userId]?.let {
//                if (now - it >= INACTIVE_TIMEOUT) {
//                    webSocketServerMap[userId]?.disconnectUser(userId)
//                    lastActiveTimeMap.remove(userId)
//                }
//            }
//        }
//    }
//}