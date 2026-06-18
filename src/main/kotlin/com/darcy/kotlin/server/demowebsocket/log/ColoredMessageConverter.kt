package com.darcy.kotlin.server.demowebsocket.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * 将消息文本按日志级别着色
 */
class ColoredMessageConverter : ClassicConverter() {

    override fun convert(event: ILoggingEvent): String {
        val colorCode = when (event.level.toInt()) {
            Level.TRACE_INT -> "\u001B[37m"   // 白色
            Level.DEBUG_INT -> "\u001B[34m"   // 蓝色
            Level.INFO_INT  -> "\u001B[32m"   // 绿色
            Level.WARN_INT  -> "\u001B[33m"   // 黄色
            Level.ERROR_INT -> "\u001B[31m"   // 红色
            else            -> ""
        }
        // 返回：颜色码 + 消息原文 + 重置码
        return colorCode + event.formattedMessage + "\u001B[0m"
    }
}