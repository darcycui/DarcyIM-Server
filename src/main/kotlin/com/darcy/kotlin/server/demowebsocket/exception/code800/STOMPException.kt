package com.darcy.kotlin.server.demowebsocket.exception.code800

import com.darcy.kotlin.server.demowebsocket.exception.BaseException
/**
 * WebSocket相关异常
 * 错误码 801-899
 */
class STOMPException(
    exceptionCode: Int,
    exceptionMessage: String
) : BaseException(exceptionCode, exceptionMessage) {
    companion object {
        val STOMP_CONNECT_FAILED = STOMPException(801, "STOMP连接失败")
        val STOMP_CONNECT_TIMEOUT = STOMPException(802, "STOMP连接超时")
        val STOMP_CONNECT_ERROR = STOMPException(803, "STOMP心跳错误")

        val STOMP_SEND_PRIVATE_MESSAGE_FAILED = STOMPException(821, "STOMP发送单聊消息失败")
        val STOMP_SEND_ALL_GROUP_MESSAGE_FAILED = STOMPException(822, "STOMP发送全部群聊消息失败")
        val STOMP_SEND_TARGET_GROUP_MESSAGE_FAILED = STOMPException(823, "STOMP发送指定群聊消息失败")
        val STOMP_SEND_PRIVATE_MESSAGE_READ_STATUS_FAILED = STOMPException(824, "STOMP发送已读状态消息失败")

        val STOMP_PRIVATE_MESSAGE_FORMAT_ERROR = STOMPException(831, "STOMP单聊消息格式错误")
        val STOMP_PRIVATE_MESSAGE_READ_STATUS_FORMAT_ERROR = STOMPException(832, "STOMP单聊消息已读状态格式错误")
    }
}