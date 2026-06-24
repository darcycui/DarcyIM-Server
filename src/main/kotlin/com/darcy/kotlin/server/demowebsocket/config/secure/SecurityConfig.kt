package com.darcy.kotlin.server.demowebsocket.config.secure

import com.darcy.kotlin.server.demowebsocket.config.jwt.JsonAccessDeniedHandler
import com.darcy.kotlin.server.demowebsocket.config.jwt.JsonAuthenticationEntryPoint
import com.darcy.kotlin.server.demowebsocket.config.jwt.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Autowired
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Autowired
    private lateinit var jsonAuthenticationEntryPoint: JsonAuthenticationEntryPoint

    @Autowired
    private lateinit var jsonAccessDeniedHandler: JsonAccessDeniedHandler

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/index.html",
                    "/user/**",
                    "/ui/**",
                    "/api/users/create",
                    "/api/login",
                    "/api/register",
                    "/api/**",
                    "/stomp-ws/**",  // 放行websocket连接
                    "/stomp-sockjs/**",
                    "/js/**",
                    "/error/**",
                ).permitAll()
                    .anyRequest().authenticated()
            }
            // 使用 httpBasic 认证
//            .httpBasic {}
            .cors{}  // 允许跨域 使用[WebMvcConfig#addCorsMappings()]的配置
            .csrf {
                // 通常API 禁用CSRF
                it.disable()
            }
            .sessionManagement {
                // 通常API 使用无状态Session
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // 添加 JWT 过滤器
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            // 添加异常处理配置
            .exceptionHandling {
                it.authenticationEntryPoint(jsonAuthenticationEntryPoint)
                it.accessDeniedHandler(jsonAccessDeniedHandler)
            }
            .build()
    }
}