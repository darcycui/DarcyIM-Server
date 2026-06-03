package com.darcy.kotlin.server.demowebsocket.config.crypto

import com.darcy.kotlin.server.demowebsocket.config.crypto.annotation.Encrypted
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
import java.io.ByteArrayInputStream
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

@ControllerAdvice
class DecryptRequestBodyAdvice : RequestBodyAdvice {

    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        // 仅处理标记了 @Encrypted 的方法或类
        return methodParameter.method?.isAnnotationPresent(Encrypted::class.java) == true
                || methodParameter.containingClass.isAnnotationPresent(Encrypted::class.java)
    }

    override fun beforeBodyRead(
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): HttpInputMessage {
        // 读取原始请求体并解密
        val originalBody = String(inputMessage.body.readAllBytes(), StandardCharsets.UTF_8)
        val decryptedBody = AesUtil.decrypt(originalBody)

        // 返回新的 HttpInputMessage，包含解密后的字节流
        return object : HttpInputMessage {
            override fun getBody() = ByteArrayInputStream(decryptedBody.toByteArray())
            override fun getHeaders() = inputMessage.headers
        }
    }

    override fun afterBodyRead(
        body: Any,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any = body

    override fun handleEmptyBody(
        body: Any?,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any? = body
}