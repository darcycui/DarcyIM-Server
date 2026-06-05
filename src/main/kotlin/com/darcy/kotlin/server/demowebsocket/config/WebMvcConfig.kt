package com.darcy.kotlin.server.demowebsocket.config

import com.darcy.kotlin.server.demowebsocket.exception.BaseException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {
    @Value("\${upload.path.image}")
    private val picDir: String? = null

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/file/**")
            .addResourceLocations("file:$picDir")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 匹配所有路径
//            .allowedOrigins("*")// 生产环境应指定具体域名
            .allowedOriginPatterns("*", "null")
            .allowedMethods("*") // 允许的方法
            .allowedHeaders("*") // 允许的头信息
            .allowCredentials(true) // 是否允许携带 Cookie
            .maxAge(30) // 预检请求的有效期（秒）
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // 找到 Jackson 的转换器
        val jacksonConverter = converters.filterIsInstance<MappingJackson2HttpMessageConverter>().firstOrNull()

        if (jacksonConverter != null) {
            // 清空默认支持的 MediaType，并设置我们想要的 MediaType
            // 这里强制指定返回 Content-Type 为 application/json;charset=UTF-8
            jacksonConverter.supportedMediaTypes = mutableListOf(MediaType("application", "json", Charsets.UTF_8))
        } else {
            throw BaseException(-1, "未找到 Jackson 的转换器")
        }
    }
}
