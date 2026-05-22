package com.darcy.kotlin.server.demowebsocket.utils

import com.alibaba.fastjson2.JSON

object JsonUtil {
    fun toJson(obj: Any?): String {
        if (obj == null) return "{}"
        return kotlin.runCatching {
            JSON.toJSONString(obj)
        }.onFailure {
            it.printStackTrace()
        }.getOrElse { "{}" }
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        return kotlin.runCatching {
            JSON.parseObject(json, clazz)
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }
}