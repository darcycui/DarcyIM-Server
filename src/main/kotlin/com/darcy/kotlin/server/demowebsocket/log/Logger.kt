package com.darcy.kotlin.server.demowebsocket.log

import com.darcy.kotlin.server.demowebsocket.DemoWebsocketApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// 全局日志 logger
val DarcyLogger: Logger = LoggerFactory.getLogger(DemoWebsocketApplication::class.java)

fun logD(message: String) {
    DarcyLogger.debug(message)
}

fun logI(message: String) {
    DarcyLogger.info(message)
}

fun logW(message: String) {
    DarcyLogger.warn(message)
}

fun logE(message: String) {
    DarcyLogger.error(message)
}