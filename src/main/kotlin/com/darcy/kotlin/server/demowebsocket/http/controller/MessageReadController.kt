package com.darcy.kotlin.server.demowebsocket.http.controller

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.api.IMessageReadApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.MessageReadStatusInputDTO
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
    override fun receiverGetUnreadMessageListByConversation(params: Map<String, String>): String {
        val userId = params["userId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val targetId = params["targetId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("targetId" to "目标ID不能为空"))
        val result = messageReadStatusService.receiverGetUnreadMessageListByConversation(userId, targetId)
        return ResultEntity.success(result).toJsonString()
    }

    override fun receiverMarkMessagesAsRead(params: Map<String, String>): String {
        val messageReadStatusInputDTOStr = params["messageReadStatusInputDTO"]
            ?: throw ParamsException.ParamsNotValid(mapOf("messageReadStatusInputDTO" to "消息参数不能为空"))
        val messageReadStatusInputDTO =
            JSON.parseObject(messageReadStatusInputDTOStr, MessageReadStatusInputDTO::class.java)
                ?: throw ParamsException.ParamsNotValid(mapOf("messageReadStatusInputDTO" to "消息参数格式错误"))
        val userId = messageReadStatusInputDTO.userId
        val msgIds = messageReadStatusInputDTO.msgIds
        val updatedCount = messageReadStatusService.receiverMarkMessagesAsRead(userId, msgIds)
        val result = messageReadStatusService.receiverGetMessageListReadStatus(userId, msgIds)
        websocket.convertAndSendToUser(
            messageReadStatusInputDTO.targetName,
            "/queue/message/read",
            result
        )
        return ResultEntity.success(result).toJsonString()
    }

    override fun senderSyncMessageStatus(params: Map<String, String>): String {
        val userId = params["userId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val targetId = params["targetId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("targetId" to "目标ID不能为空"))
        val result = messageReadStatusService.senderSyncMessageReadStatus(userId, targetId)
        return ResultEntity.success(result).toJsonString()
    }

}