package com.darcy.kotlin.server.demowebsocket.config.advice

import com.darcy.kotlin.server.demowebsocket.config.jwt.JwtTokenProvider
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.crypto.transport.ITransportCipher
import com.darcy.kotlin.server.demowebsocket.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logD
import com.darcy.kotlin.server.demowebsocket.utils.TokenUtil
import com.darcy.kotlin.server.demowebsocket.utils.hexStrToBytes
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
import java.io.ByteArrayInputStream
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

@ControllerAdvice
class DecryptRequestBodyAdvice @Autowired constructor(
    private val tokenProvider: JwtTokenProvider,
    private val userService: UserService,
) : RequestBodyAdvice {
    companion object {
        private val TAG = DecryptRequestBodyAdvice::class.java.simpleName
    }

    private val transformCipher: ITransportCipher = ChaCha20TransportCipher

    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        // 仅处理标记了 @Encrypted 的方法或类
        val isEncrypted = methodParameter.method?.isAnnotationPresent(Encrypted::class.java) == true
                || methodParameter.containingClass.isAnnotationPresent(Encrypted::class.java)
        logD("$TAG 是否需要拦截请求: $isEncrypted")
        return isEncrypted
    }

    override fun beforeBodyRead(
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): HttpInputMessage {
        logD("$TAG 拦截请求...")
        val servletRequest: HttpServletRequest = when (inputMessage) {
            is ServletServerHttpRequest -> inputMessage.servletRequest
            else -> {
                val attrs = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
                attrs.request
            }
        }
        if (EncryptResponseBodyAdvice.noNeedEncrypt(servletRequest.requestURI)) {
            logD("$TAG 请求未加密，不进行解密")
            return inputMessage
        }
        val token = servletRequest.getHeader(TokenUtil.TOKEN_HEADER)
        val username = tokenProvider.getUsernameFromJWT(TokenUtil.cutOnlyToken(token))
        val userId = userService.queryUserByUsername(username).id
        // 读取原始请求体并解密
        val originalBody = inputMessage.body.readAllBytes()
        logD("$TAG 原始请求body: ${originalBody.decodeToString()}")
        val aad = "${servletRequest.method}:${servletRequest.requestURL}"
        val decryptedBody = transformCipher.decrypt(
            userId = userId,
            ciphertext = originalBody.decodeToString().hexStrToBytes(), // 密文是16进制字符串
            aad = aad.toByteArray(StandardCharsets.UTF_8),
        )
        logD("$TAG 解密后请求body: ${String(decryptedBody)}")

        // 返回新的 HttpInputMessage，包含解密后的字节流
        return object : HttpInputMessage {
            override fun getBody() = ByteArrayInputStream(decryptedBody)
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