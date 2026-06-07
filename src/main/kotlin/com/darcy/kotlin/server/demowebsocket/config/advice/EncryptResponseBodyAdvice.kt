package com.darcy.kotlin.server.demowebsocket.config.advice

import com.darcy.kotlin.server.demowebsocket.config.jwt.JwtTokenProvider
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.crypto.transport.ITransportCipher
import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.utils.JsonUtil
import com.darcy.kotlin.server.demowebsocket.utils.TokenUtil
import com.darcy.kotlin.server.demowebsocket.utils.bytesToHexStr
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Priority
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
@Priority(3) //  优先级低于 UnifiedResponseAdvice
class EncryptResponseBodyAdvice @Autowired constructor(
    private val tokenProvider: JwtTokenProvider,
    private val userService: UserService,
) : ResponseBodyAdvice<Any?> {
    companion object {
        // 日志标签
        private val TAG = EncryptResponseBodyAdvice::class.simpleName
    }

    private val transformCipher: ITransportCipher = ChaCha20TransportCipher

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        // 1. 检查方法或类是否有 @Encrypted
        val hasEncryptedAnnotation = returnType.method?.isAnnotationPresent(Encrypted::class.java) == true
                || returnType.containingClass.isAnnotationPresent(Encrypted::class.java)
        // 2. 检查是否是异常处理器（可选）
        val isExceptionHandler = returnType.method?.isAnnotationPresent(ExceptionHandler::class.java) == true
        val needEncrypt = hasEncryptedAnnotation || isExceptionHandler
        DarcyLogger.debug("$TAG Need encrypt: $needEncrypt")
        return needEncrypt
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        DarcyLogger.debug("$TAG Encrypting response body...")
        if (body == null) {
            DarcyLogger.debug("$TAG Response body is null")
            return null
        }
        if (body is String) {
            DarcyLogger.debug("$TAG Response body is String return it directly")
            return body
        }
        val realRequest = (request as ServletServerHttpRequest)
        val token = realRequest.servletRequest.getHeader(TokenUtil.TOKEN_HEADER)
        val username = tokenProvider.getUsernameFromJWT(TokenUtil.cutOnlyToken(token))
        val userId = userService.queryUserByUsername(username).id
        // 将响应对象转为 JSON 字符串
        val json = JsonUtil.toJson(body)
        DarcyLogger.debug("$TAG Original Response body: $body")
        // 加密后返回加密字符串
        val ciphertext = transformCipher.encrypt(
            userId = userId,
            plaintext = json.toByteArray(),
            aad = "${realRequest.method}:${realRequest.uri}".toByteArray()
        ).bytesToHexStr()
        DarcyLogger.debug("$TAG Encrypted Response body: $ciphertext")
        return ciphertext
    }
}