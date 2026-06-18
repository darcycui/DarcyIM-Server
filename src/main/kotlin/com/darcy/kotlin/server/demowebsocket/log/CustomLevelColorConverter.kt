package com.darcy.kotlin.server.demowebsocket.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * 自定义级别颜色转换器
 */
class CustomLevelColorConverter : ClassicConverter() {
    override fun convert(event: ILoggingEvent): String {
        val colorCode = when (event.level.toInt()) {
            Level.TRACE_INT -> "\u001B[37m" // 白色
            Level.DEBUG_INT -> "\u001B[34m" // 蓝色
            Level.INFO_INT -> "\u001B[32m" // 绿色
            Level.WARN_INT -> "\u001B[33m" // 黄色
            Level.ERROR_INT -> "\u001B[31m" // 红色
            else -> ""
        }
        // 返回 "颜色码 + 级别名称 + 重置码"
        return colorCode + event.level.toString() + "\u001B[0m"
    }
}