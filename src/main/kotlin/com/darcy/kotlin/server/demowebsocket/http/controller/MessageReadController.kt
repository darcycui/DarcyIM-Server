package com.darcy.kotlin.server.demowebsocket.http.controller

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.api.IMessageReadApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.MessageReadStatusInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.MessageReadStatusDTO
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.http.service.MessageReadStatusService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageReadController @Autowired constructor(
    private val messageReadStatusService: MessageReadStatusService,
    private val websocket: SimpMessagingTemplate
) : IMessageReadApi {
    override fun markMessageRead(params: Map<String, String>): String {
        val messageReadStatusInputDTOStr = params["messageReadStatusInputDTO"]
            ?: throw ParamsException.ParamsNotValid(mapOf("messageReadStatusInputDTO" to "消息参数不能为空"))
        val messageReadStatusInputDTO = JSON.parseObject(messageReadStatusInputDTOStr, MessageReadStatusInputDTO::class.java)
            ?: throw ParamsException.ParamsNotValid(mapOf("messageReadStatusInputDTO" to "消息参数格式错误"))
        val updatedCount = messageReadStatusService.markMessagesAsRead(messageReadStatusInputDTO.userId, messageReadStatusInputDTO.msgIds)
        val result = messageReadStatusInputDTO.msgIds.mapNotNull {
            messageReadStatusService.getMessageReadStatus(it, messageReadStatusInputDTO.userId)
        }
        websocket.convertAndSendToUser(
            messageReadStatusInputDTO.targetName,
            "/queue/message/read",
            result
        )
        return ResultEntity.success(result).toJsonString()
    }

    override fun getUnreadCount(params: Map<String, String>): String {
        TODO("待实现")
    }

}