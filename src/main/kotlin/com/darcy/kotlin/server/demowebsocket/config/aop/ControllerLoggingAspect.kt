package com.darcy.kotlin.server.demowebsocket.config.aop

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logE
import com.darcy.kotlin.server.demowebsocket.log.logI
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile

@Aspect
@Component
class ControllerLoggingAspect {

    private val objectMapper = ObjectMapper().apply {
        // 美化输出
        enable(SerializationFeature.INDENT_OUTPUT)
        findAndRegisterModules()
    }

    @Around("execution(* com.darcy.kotlin.server.demowebsocket.http.controller.*.*(..))")
    fun logControllerMethods(joinPoint: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()
        val signature = joinPoint.signature as MethodSignature
        val methodName = "${signature.declaringType.simpleName}.${signature.name}"

        val requestHeaders = getRequestHeaders()
        logI("REQUEST: $methodName $methodName")
        logI("Headers: $requestHeaders")
        val requestBody = if (shouldSkipRequestBody(joinPoint)) {
            "(skipped - contains special parameters)"
        } else {
            getRequestBody()
        }
        val formParams = getFormParameters()
        if (formParams.isNotEmpty()) {
            logI("Form Params: $formParams")
        } else {
            logI("Body: $requestBody")
        }

        return try {
            // 执行方法
            val result = joinPoint.proceed()
            // 记录响应结果
            logI(
                "RESPONSE: $methodName | Result: ${formatJson(result)} | Time: ${System.currentTimeMillis() - startTime}ms",
            )
            result
        } catch (e: Exception) {
            logE("ERROR in $methodName | ${e.message}")
            throw e
        }
    }

    private fun getFormParameters(): String {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            val request = requestAttributes?.request

            if (request != null) {
                val contentType = request.contentType ?: ""
                if (contentType.contains("application/x-www-form-urlencoded", ignoreCase = true)) {
                    val params = mutableMapOf<String, String>()
                    val parameterNames = request.parameterNames
                    while (parameterNames.hasMoreElements()) {
                        val paramName = parameterNames.nextElement()
                        val paramValue = request.getParameter(paramName)
                        params[paramName] = paramValue ?: ""
                    }

                    if (params.isNotEmpty()) {
                        return formatJson(params)
                    }
                }
            }
            ""
        } catch (e: Exception) {
            "(error reading form params: ${e.message})"
        }
    }


    private fun shouldSkipRequestBody(joinPoint: ProceedingJoinPoint): Boolean {
        val args = joinPoint.args
        for (arg in args) {
            if (arg is HttpServletRequest
                || arg is HttpServletResponse
                || arg is MultipartFile
                || (arg is Array<*> && arg.any { it is MultipartFile })) {
                return true
            }
        }
        return false
    }

    private fun getRequestHeaders(): String {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            val request = requestAttributes?.request

            if (request != null) {
                val headers = mutableMapOf<String, String>()
                val headerNames = request.headerNames
                while (headerNames.hasMoreElements()) {
                    val headerName = headerNames.nextElement()
                    val headerValue = request.getHeader(headerName)
                    headers[headerName] = headerValue ?: ""
                }
                formatJson(headers)
            } else {
                "(no request)"
            }
        } catch (e: Exception) {
            "(error reading headers: ${e.message})"
        }
    }


    private fun getRequestBody(): String {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            val request = requestAttributes?.request

            if (request != null) {
                val contentType = request.contentType ?: ""
                if (contentType.contains("application/x-www-form-urlencoded", ignoreCase = true)) {
                    return "(form data - see Form Params)"
                }

                val body = request.inputStream.readBytes().toString(Charsets.UTF_8)
                if (body.isNotBlank()) {
                    formatJson(body)
                } else {
                    "(empty)"
                }
            } else {
                "(no request)"
            }
        } catch (e: Exception) {
            "(error reading body: ${e.message})"
        }
    }

    private fun getMethodParameters(joinPoint: ProceedingJoinPoint): String {
        val signature = joinPoint.signature as MethodSignature
        val parameterNames = signature.parameterNames
        val args = joinPoint.args
        val params = mutableMapOf<String, Any?>()

        for (i in args.indices) {
            val arg = args[i]
            // 忽略特殊参数
            if (arg is HttpServletRequest || arg is HttpServletResponse
                || arg is MultipartFile || arg is Array<*> && arg.isArrayOf<MultipartFile>()) {
                continue
            }

            params[parameterNames[i]] = arg
        }

        return formatJson(params)
    }

    private fun formatJson(obj: Any?): String {
        return try {
            when (obj) {
                null -> "null"
                is String -> {
                    // 尝试解析字符串是否为JSON
                    try {
                        if ((obj.trimStart().startsWith("{") && obj.trimEnd().endsWith("}"))
                            || (obj.trimStart().startsWith("[") && obj.trimEnd().endsWith("]"))) {
                            val jsonNode = objectMapper.readTree(obj)
                            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
                        }
                    } catch (e: Exception) {
                        // 不是JSON，返回原字符串
                    }
                    obj
                }
                else -> objectMapper.writeValueAsString(obj)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "JSON_ERROR: ${e.message}"
        }
    }
}
