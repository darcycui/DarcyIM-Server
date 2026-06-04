package com.darcy.kotlin.server.demowebsocket

import com.darcy.kotlin.server.demowebsocket.domain.dto.input.LoginRequestDTO
import com.darcy.kotlin.server.demowebsocket.utils.HashUtil
import com.darcy.kotlin.server.demowebsocket.utils.JsonUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LoginTests {

    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test-login-v2`() {
        val loginRequest = LoginRequestDTO(
            phone = "156000111222",
            password = HashUtil.sha256Str("123456")
//            phone = "",
//            password = ""
        )
        val result = mockMvc.perform(
            post("http://localhost:$port/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }
}