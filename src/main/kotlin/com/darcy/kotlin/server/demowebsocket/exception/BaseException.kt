package com.darcy.kotlin.server.demowebsocket.exception

import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity

open class BaseException(
    val exceptionCode: Int = -1,
    var exceptionMessage: String
) : IllegalStateException("exceptionCode=$exceptionCode exceptionMessage=$exceptionMessage") {
    companion object {
        val UNKNOWN_THROWABLE = BaseException(101, "其他异常")
        val UNKNOWN_EXCEPTION = BaseException(102, "其他异常")
    }
}

fun BaseException.toJsonString(): String {
    return ResultEntity.error(this).toJsonString()
}