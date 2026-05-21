package com.darcy.kotlin.server.demowebsocket.config.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import java.text.SimpleDateFormat

/**
 * 统一处理 controller的返回结果 object-->JSON
 */
@Configuration
class JsonConfig {

//    @Bean
//    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
//        val objectMapper = ObjectMapper()
//
//        objectMapper.apply {
//            registerModule(JavaTimeModule())
//            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//            setDateFormat(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
//        }
//
//        return MappingJackson2HttpMessageConverter(objectMapper)
//    }
}
