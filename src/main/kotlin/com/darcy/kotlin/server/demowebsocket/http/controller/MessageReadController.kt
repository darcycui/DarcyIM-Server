package com.darcy.kotlin.server.demowebsocket.http.controller

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.api.IMessageReadApi
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverOfflineMessageSyncInputDTO
import com.darcy.kotlin.server.demowebsocket.domain.dto.message.toDTO
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
    // 新增：接收方离线消息同步
    override fun receiverPullOfflineMessages(params: Map<String, String>): String {
        val userId = params["userId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val targetId = params["targetId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("targetId" to "目标ID不能为空"))
        val conversationId = params["conversationId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("conversationId" to "会话ID不能为空"))
        val conversationType = params["conversationType"]?.toIntOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("conversationType" to "会话类型不能为空"))
        val input = ReceiverOfflineMessageSyncInputDTO(
            userId = userId,
            targetId = targetId,
            conversationId = conversationId,
            conversationType = conversationType,
            lastMsgId = params["lastMsgId"],
            lastSyncTime = params["lastSyncTime"],
            page = params["page"]?.toIntOrNull(),
            size = params["size"]?.toIntOrNull(),
            deviceId = params["deviceId"] ?: "",
            clientType = params["clientType"] ?: ""
        )

        val result = messageReadStatusService.receiverSyncOfflineMessages(input)
        return ResultEntity.success(result.toDTO()).toJsonString()
    }


    override fun receiverMarkMessagesAsRead(params: Map<String, String>): String {
        val messageReadStatusInputDTOStr = params["messageReadStatusInputDTO"]
            ?: throw ParamsException.ParamsNotValid(mapOf("messageReadStatusInputDTO" to "消息参数不能为空"))
        val receiverMessageReadStatusMarkInputDTO =
            JSON.parseObject(messageReadStatusInputDTOStr, ReceiverMessageReadStatusMarkInputDTO::class.java)
                ?: throw ParamsException.ParamsNotValid(mapOf("messageReadStatusInputDTO" to "消息参数格式错误"))
        val conversationId = receiverMessageReadStatusMarkInputDTO.conversationId
        if (conversationId == 0L) {
            throw ParamsException.ParamsNotValid(mapOf("conversationId" to "会话ID不能为空"))
        }
        val userId = receiverMessageReadStatusMarkInputDTO.userId
        val msgIds = receiverMessageReadStatusMarkInputDTO.msgIds
        val updatedCount = messageReadStatusService.receiverMarkMessagesAsRead(userId, msgIds)
        val result = messageReadStatusService.receiverGetMessageListReadStatus(userId, msgIds)
        websocket.convertAndSendToUser(
            receiverMessageReadStatusMarkInputDTO.targetName,
            "/queue/message/read",
            result
        )
        return ResultEntity.success(result).toJsonString()
    }

    // 新增：发送方离线已读状态同步
    override fun senderSyncOfflineMessageReadStatus(params: Map<String, String>): String {
        val userId = params["userId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("userId" to "用户ID不能为空"))
        val targetId = params["targetId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("targetId" to "目标ID不能为空"))
        val conversationId = params["conversationId"]?.toLongOrNull()
            ?: throw ParamsException.ParamsNotValid(mapOf("conversationId" to "会话ID不能为空"))
        // 离线开始时间
        var since = params["since"]
        if (since?.isEmpty() == true) {
            since = null
        }
        // 离线结束时间 (通常是当前时间)
        var until = params["until"]
        if (until?.isEmpty() == true) {
            until = null
        }
        val result = messageReadStatusService.senderSyncOfflineMessageReadStatus(
            userId, targetId, since, until
        )
        return ResultEntity.success(result).toJsonString()
    }
}