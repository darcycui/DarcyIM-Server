package com.darcy.kotlin.server.demowebsocket.config.advice//package com.darcy.kotlin.server.demowebsocket.config.crypto
//
//import com.darcy.kotlin.server.demowebsocket.config.crypto.annotation.Encrypted
//import com.darcy.kotlin.server.demowebsocket.config.crypto.transport.ITransportCipher
//import com.darcy.kotlin.server.demowebsocket.config.crypto.transport.impl.ChaCha20TransportCipher
//import com.darcy.kotlin.server.demowebsocket.config.jwt.JwtTokenProvider
//import com.darcy.kotlin.server.demowebsocket.http.service.UserService
//import com.darcy.kotlin.server.demowebsocket.utils.TokenUtil
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.core.MethodParameter
//import org.springframework.http.HttpInputMessage
//import org.springframework.http.converter.HttpMessageConverter
//import org.springframework.http.server.ServletServerHttpRequest
//import org.springframework.web.bind.annotation.ControllerAdvice
//import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
//import java.io.ByteArrayInputStream
//import java.lang.reflect.Type
//import java.nio.charset.StandardCharsets
//
//@ControllerAdvice
//class DecryptRequestBodyAdvice @Autowired constructor(
//    private val tokenProvider: JwtTokenProvider,
//    private val userService: UserService,
//) : RequestBodyAdvice {
//    private val transformCipher: ITransportCipher = ChaCha20TransportCipher
//
//    override fun supports(
//        methodParameter: MethodParameter,
//        targetType: Type,
//        converterType: Class<out HttpMessageConverter<*>>
//    ): Boolean {
//        // 仅处理标记了 @Encrypted 的方法或类
//        return methodParameter.method?.isAnnotationPresent(Encrypted::class.java) == true
//                || methodParameter.containingClass.isAnnotationPresent(Encrypted::class.java)
//    }
//
//    override fun beforeBodyRead(
//        inputMessage: HttpInputMessage,
//        parameter: MethodParameter,
//        targetType: Type,
//        converterType: Class<out HttpMessageConverter<*>>
//    ): HttpInputMessage {
//        val request = (inputMessage as ServletServerHttpRequest)
//        val token = request.servletRequest.getHeader(TokenUtil.TOKEN_HEADER)
//        val username = tokenProvider.getUsernameFromJWT(TokenUtil.cutOnlyToken(token))
//        val userId = userService.queryUserByUsername(username).id
//        // 读取原始请求体并解密
//        val originalBody = inputMessage.body.readAllBytes()
//        val decryptedBody = transformCipher.decrypt(
//            userId = userId,
//            ciphertext = originalBody,
//            aad = "${request.method}:${request.uri.path}".toByteArray(StandardCharsets.UTF_8),
//        )
//
//        // 返回新的 HttpInputMessage，包含解密后的字节流
//        return object : HttpInputMessage {
//            override fun getBody() = ByteArrayInputStream(decryptedBody)
//            override fun getHeaders() = inputMessage.headers
//        }
//    }
//
//    override fun afterBodyRead(
//        body: Any,
//        inputMessage: HttpInputMessage,
//        parameter: MethodParameter,
//        targetType: Type,
//        converterType: Class<out HttpMessageConverter<*>>
//    ): Any = body
//
//    override fun handleEmptyBody(
//        body: Any?,
//        inputMessage: HttpInputMessage,
//        parameter: MethodParameter,
//        targetType: Type,
//        converterType: Class<out HttpMessageConverter<*>>
//    ): Any? = body
//}