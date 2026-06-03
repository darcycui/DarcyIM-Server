package com.darcy.kotlin.server.demowebsocket.config.crypto

import com.darcy.kotlin.server.demowebsocket.config.crypto.annotation.Encrypted
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class EncryptResponseBodyAdvice : ResponseBodyAdvice<Any?> {

    private val objectMapper = ObjectMapper()

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out org.springframework.http.converter.HttpMessageConverter<*>>
    ): Boolean {
        // 仅处理标记了 @Encrypted 的方法或类
        return returnType.method?.isAnnotationPresent(Encrypted::class.java) == true
                || returnType.containingClass.isAnnotationPresent(Encrypted::class.java)
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out org.springframework.http.converter.HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        if (body == null) return null

        // 将响应对象转为 JSON 字符串
        val json = objectMapper.writeValueAsString(body)
        // 加密后返回加密字符串
        return AesUtil.encrypt(json)
    }
}