//package com.darcy.kotlin.server.demowebsocket.websocket.helper
//
//import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
//import com.darcy.kotlin.server.demowebsocket.websocket.server.WebSocketServer
//import java.io.IOException
//import jakarta.websocket.Session
//
//object SendHelper {
//    fun sendPing(session: Session?) {
//        try {
//            logI("Server sending ping...")
//            if (session?.isOpen != true) {
//                logI("Error: session is closed.")
//                return
//            }
//            session.basicRemote?.sendText("ping")
//        } catch (e: IOException) {
//            e.printStackTrace()
//            logI("ping-->send message error: ${e.message}")
//        }
//    }
//
//    fun sendPong(session: Session?) {
//        try {
//            logI("Server sending pong...")
//            if (session?.isOpen != true) {
//                logI("Error: session is closed.")
//                return
//            }
//            session.basicRemote?.sendText("pong")
//        } catch (e: IOException) {
//            e.printStackTrace()
//            logI("pang-->send message error: ${e.message}")
//        }
//    }
//
//    /**
//     * 发送文字给其他用户
//     *
//     * @param message 发送信息
//     * @param session 目的session
//     */
//    fun sendToPersonStr(message: String?, session: Session?) {
//        try {
//            logI("发送文字给其他用户: $message")
//            if (session?.isOpen != true) {
//                logI("Error: session is closed.")
//                return
//            }
//            session.basicRemote?.sendText(message) ?: run {
//                logI("session is null")
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            logI("send message error: ${e.message}")
//        }
//    }
//
//    /**
//     * 发送文字给群组
//     *
//     * @param message          发送信息
//     * @param webSocketServers 目的群组
//     */
//    fun sendToGroupStr(message: String?, webSocketServers: List<WebSocketServer?>) {
//        webSocketServers.forEach { ws: WebSocketServer? ->
//            sendToPersonStr(message, ws?.getSession())
//        }
//    }
//}