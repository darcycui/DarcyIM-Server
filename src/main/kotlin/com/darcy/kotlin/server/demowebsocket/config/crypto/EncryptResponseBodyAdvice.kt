package com.darcy.kotlin.server.demowebsocket.config.crypto

import com.darcy.kotlin.server.demowebsocket.config.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.config.crypto.transport.ITransportCipher
import com.darcy.kotlin.server.demowebsocket.config.crypto.transport.impl.ChaCha20TransportCipher
import com.darcy.kotlin.server.demowebsocket.config.jwt.JwtTokenProvider
import com.darcy.kotlin.server.demowebsocket.http.service.UserService
import com.darcy.kotlin.server.demowebsocket.utils.TokenUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class EncryptResponseBodyAdvice @Autowired constructor(
    private val tokenProvider: JwtTokenProvider,
    private val userService: UserService,
) : ResponseBodyAdvice<Any?> {

    private val transformCipher: ITransportCipher = ChaCha20TransportCipher
    private val objectMapper = ObjectMapper()

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        // 仅处理标记了 @Encrypted 的方法或类
        return returnType.method?.isAnnotationPresent(Encrypted::class.java) == true
                || returnType.containingClass.isAnnotationPresent(Encrypted::class.java)
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        if (body == null) return null
        val request = (request as ServletServerHttpRequest)
        val token = request.servletRequest.getHeader(TokenUtil.TOKEN_HEADER)
        val username = tokenProvider.getUsernameFromJWT(TokenUtil.cutOnlyToken(token))
        val userId = userService.queryUserByUsername(username).id
        // 将响应对象转为 JSON 字符串
        val json = objectMapper.writeValueAsString(body)
        // 加密后返回加密字符串
        return transformCipher.encrypt(
            userId = userId,
            plaintext = json.toByteArray(),
            aad = "${request.method}:${request.uri}".toByteArray()
        )
    }
}