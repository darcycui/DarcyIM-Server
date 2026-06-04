package com.darcy.kotlin.server.demowebsocket.config.advice

import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.exception.BaseException
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 全局异常处理
 */
@RestControllerAdvice(
)
class GlobalExceptionAdvice {
    // 处理自定义 BaseException 异常
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResultEntity<*> {
        DarcyLogger.error("handleBaseException:${ex::class.simpleName}")
        ex.printStackTrace()
        return ResultEntity.error(ex)
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResultEntity<*> {
        DarcyLogger.error("handleValidationException:${ex::class.simpleName}")
        ex.printStackTrace()
        val errors = ex.bindingResult?.fieldErrors?.map {
            mapOf(it.field to (it.defaultMessage ?: "参数不合法"))
        }
        val errorMaps = errors?.reduce { acc, map ->
            acc.plus(map)
        }
        return ResultEntity.error(
            ParamsException.ParamsNotValid(errorMaps ?: emptyMap())
        )
    }

    // 处理其他异常 Exception
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResultEntity<*> {
        DarcyLogger.error("handleException:${ex::class.simpleName}")
        ex.printStackTrace()
        return ResultEntity.error(BaseException.UNKNOWN_EXCEPTION.apply {
            exceptionMessage += ":${ex::class.simpleName} :${ex.message}"
        })
    }
}