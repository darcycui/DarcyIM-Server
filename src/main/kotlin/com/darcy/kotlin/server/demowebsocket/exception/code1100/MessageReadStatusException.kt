package com.darcy.kotlin.server.demowebsocket.exception.code1100

import com.darcy.kotlin.server.demowebsocket.exception.BaseException

class MessageReadStatusException(
    exceptionCode: Int,
    exceptionMessage: String
) : BaseException(exceptionCode, exceptionMessage) {
    companion object {
        val MESSAGE_READ_STATUS_NOT_EXIST = MessageReadStatusException(1101, "消息已读状态不存在")
    }
}