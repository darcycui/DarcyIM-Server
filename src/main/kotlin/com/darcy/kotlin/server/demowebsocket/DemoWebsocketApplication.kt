package com.darcy.kotlin.server.demowebsocket

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class DemoWebsocketApplication

fun main(args: Array<String>) {
    runApplication<DemoWebsocketApplication>(*args)
    DarcyLogger.trace("DarcyLogger trace")
    DarcyLogger.info("DarcyLogger info")
    DarcyLogger.debug("DarcyLogger debug")
    DarcyLogger.warn("DarcyLogger warn")
    DarcyLogger.error("DarcyLogger error")
}
