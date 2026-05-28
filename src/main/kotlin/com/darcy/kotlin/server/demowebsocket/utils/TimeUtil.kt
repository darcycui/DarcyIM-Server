package com.darcy.kotlin.server.demowebsocket.utils

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TimeUtil {
    private const val TIME_FORMATTER_1: String = "yyyy-MM-dd HH:mm:ss.SSS"
    private const val TIME_FORMATTER_2: String = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    private const val TIME_FORMATTER_3: String = "yyyy/MM/dd HH:mm:ss.SSS"

    fun parseStringToDateTime(dateStr: String): LocalDateTime {
        val formats = listOf(
            DateTimeFormatter.ofPattern(TIME_FORMATTER_1),
            DateTimeFormatter.ofPattern(TIME_FORMATTER_2),
            DateTimeFormatter.ofPattern(TIME_FORMATTER_3),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        )

        for (format in formats) {
            try {
                return LocalDateTime.parse(dateStr, format)
            } catch (e: Exception) {
                // e.printStackTrace()
                continue
            }
        }
        DarcyLogger.error("无法解析日期时间格式: $dateStr")
        return defaultDateTime()
    }

    fun formatDateTimeToString(dateTime: LocalDateTime?): String {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return if (dateTime == null) {
            defaultDateTime().format(formatter)
        } else {
            dateTime.format(formatter)
        }
    }

    fun getCurrentTimeString(): String {
        val now = LocalDateTime.now()
        return formatDateTimeToString(now)
    }

    fun defaultDateTime(): LocalDateTime {
        return LocalDateTime.of(1970, 1, 1, 0, 0, 0)
    }
}