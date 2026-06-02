package com.darcy.kotlin.server.demowebsocket.domain

import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.JSONWriter
import com.alibaba.fastjson2.annotation.JSONField
import com.darcy.kotlin.server.demowebsocket.domain.error.ErrorEntity
import com.darcy.kotlin.server.demowebsocket.exception.BaseException

/**
 * {
 *     "resultcode": "200",
 *     "reason": "success",
 *     "result": {
 *         "Country": "中国",
 *         "Province": "",
 *         "City": "",
 *         "District": "",
 *         "Isp": "Zenlayer Inc"
 *     },
 *     "error_code": 0
 * }
 */
open class ResultEntity<T>() {
    // 指定json序列化顺序
    @JSONField(ordinal = 1)
    var resultcode: Int = 200

    @JSONField(ordinal = 2)
    var result: T? = null

    @JSONField(ordinal = 3)
    var error_code: Int = 0

    @JSONField(ordinal = 4)
    var reason: String = ""

    companion object {
        fun <T> success(data: T?): ResultEntity<T> {
            return ResultEntity<T>().apply {
                this.resultcode = 200
                this.result = data
            }
        }

        fun error(errorEntity: ErrorEntity): ResultEntity<ErrorEntity> {
            return ResultEntity<ErrorEntity>().apply {
                this.resultcode = 200
//                this.reason = errorEntity.message
                this.error_code = errorEntity.status
                this.result = errorEntity
            }
        }

        fun error(e: BaseException): ResultEntity<ErrorEntity> {
            val errorEntity = ErrorEntity(
                status = e.exceptionCode,
                message = e.exceptionMessage
            )
            return error(errorEntity)
        }
    }

    fun toJsonString(): String {
        return JSONObject.toJSONString(
            this,
//            JSONWriter.Feature.ReferenceDetection
        ).also {
//            DarcyLogger.warn(it)
        }
    }

    fun toJsonStringOnlyResult(): String {
        return JSONObject.toJSONString(
            this.result,
//            JSONWriter.Feature.ReferenceDetection
        ).also {
//            DarcyLogger.warn(it)
        }
    }

    override fun toString(): String {
        return "ResultEntity(resultcode=$resultcode, result=$result, error_code=$error_code, reason='$reason')"
    }

}
