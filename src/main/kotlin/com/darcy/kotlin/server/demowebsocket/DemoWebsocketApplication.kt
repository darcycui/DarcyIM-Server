package com.darcy.kotlin.server.demowebsocket

import com.darcy.kotlin.server.demowebsocket.log.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class DemoWebsocketApplication

fun main(args: Array<String>) {
    runApplication<DemoWebsocketApplication>(*args)
    logD("DarcyLogger trace")
    logI("DarcyLogger info")
    logD("DarcyLogger debug")
    logW("DarcyLogger warn")
    logE("DarcyLogger error")
}
