package com.darcy.kotlin.server.demowebsocket.config.advice

import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import jakarta.annotation.Priority
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
@Priority(1) // 优先级高于 @EncryptResponseBodyAdvice
@Encrypted  // 添加此注解，使异常响应也能被加密
class UnifiedResponseAdvice : ResponseBodyAdvice<Any?> {
    companion object {
        private val TAG = UnifiedResponseAdvice::class.simpleName
    }

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        // 判断是否需要包装：只对非 ApiResult 类型、非 String 类型的返回值生效
        val needWrap = returnType.method?.returnType?.isAssignableFrom(ResultEntity::class.java) == false
        DarcyLogger.warn("$TAG Need UnifiedWrap: $needWrap")
        return needWrap
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        // 如果 body 已经是 ApiResult，则直接返回（避免二次包装）
        if (body is ResultEntity<*>) {
            DarcyLogger.warn("$TAG body is ResultEntity 直接返回")
            return body
        }
        // 包装为统一格式
        DarcyLogger.warn("$TAG body is ${body?.javaClass?.simpleName} 包装为统一格式 ResultEntity")
        return ResultEntity.success(body)
    }
}