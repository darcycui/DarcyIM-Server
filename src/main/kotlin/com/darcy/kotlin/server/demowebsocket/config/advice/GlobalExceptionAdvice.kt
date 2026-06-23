package com.darcy.kotlin.server.demowebsocket.config.advice

import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.exception.BaseException
import com.darcy.kotlin.server.demowebsocket.exception.code600.ParamsException
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logE
import jakarta.annotation.Priority
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 全局异常处理
 */
@RestControllerAdvice()
@Priority(2) // 优先级高于 @EncryptResponseBodyAdvice
@Encrypted  // 添加此注解，使异常响应也能被加密
// todo: 继承 ResponseEntityExceptionHandler
class GlobalExceptionAdvice {
    companion object {
        private val TAG = GlobalExceptionAdvice::class.simpleName
    }

    // 处理自定义 BaseException 异常
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResultEntity<*> {
        logE("$TAG handleBaseException:${ex::class.simpleName}")
        ex.printStackTrace()
        return ResultEntity.error(ex)
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResultEntity<*> {
        logE("$TAG handleValidationException:${ex::class.simpleName}")
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
        logE("$TAG handleException:${ex::class.simpleName}")
        ex.printStackTrace()
        return ResultEntity.error(BaseException.UNKNOWN_EXCEPTION.apply {
            exceptionMessage += ":${ex::class.simpleName} :${ex.message}"
        })
    }

    // 处理 Throwable（最顶层的异常）
    @ExceptionHandler(Throwable::class)
    fun handleThrowable(ex: Throwable): ResultEntity<*> {
        logE("$TAG handleThrowable: ${ex::class.simpleName} - ${ex.message}")
        ex.printStackTrace()
        val result = ResultEntity.error(BaseException.UNKNOWN_THROWABLE.apply {
            exceptionMessage += ":${ex::class.simpleName} :${ex.message}"
        })
        return result
    }
}