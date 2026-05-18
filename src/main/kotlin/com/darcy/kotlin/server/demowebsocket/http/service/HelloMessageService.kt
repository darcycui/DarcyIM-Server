package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.HelloMessage
import com.darcy.kotlin.server.demowebsocket.http.repository.HelloMessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HelloMessageService @Autowired constructor(
    private val helloMessageRepository: HelloMessageRepository,
) {
    fun createHelloMessage(helloMessage: HelloMessage): HelloMessage {
        return helloMessageRepository.save(helloMessage)
    }

    fun queryHelloMessage(fromUserId: Long, toUserId: Long): List<HelloMessage> {
        return helloMessageRepository.findByUserId(fromUserId, toUserId)
    }
}